package dhkthn.p2p.controller;

import dhkthn.p2p.model.storeTest.IPeerFileTransfer2;
import dhkthn.p2p.model.message.ChatMessage;
import dhkthn.p2p.service.message.ChatHistoryService;
import dhkthn.p2p.service.message.MessageServer;
import dhkthn.p2p.service.message.MessageService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List; // 🆕 THÊM IMPORT

public class ChatController {

    // CÁC COMPONENT UI TỪ FXML
    @FXML private ListView<String> userListView;
    @FXML private VBox messageContainer;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextField messageInput;
    @FXML private VBox infoPane;
    @FXML private Button infoButton;
    @FXML private BorderPane chatBorderPane;
    @FXML private Label chatName;
    @FXML private Label chatStatus;

    // SERVICES MỚI
    private MessageService messageService;
    private MessageServer messageServer;
    private ChatHistoryService chatHistoryService; // 🆕 SERVICE LỊCH SỬ
    
    // STATE
    private boolean isInfoPaneVisible = false;
    private String currentChatUser;
    private String currentPeerKey; // 🆕 LƯU KEY CỦA PEER ĐANG CHAT
    private int myPort = 12345; // UserB port

    @FXML
    public void initialize(IPeerFileTransfer2 iPeer) {
        System.out.println("🎬 Khởi tạo ChatController...");
        // 🆕 KHỞI TẠO SERVICE LỊCH SỬ
        chatHistoryService = new ChatHistoryService();
        
        setupUserList();
        initializeServices();
        setupEventHandlers();
        startMessageServer();
        
        addSystemMessage("🚀 P2P Chat đã sẵn sàng!");
        addSystemMessage("Chọn một người từ danh sách để bắt đầu chat");
        
        System.out.println("✅ ChatController khởi tạo hoàn tất");
    }

    private void initializeServices() {
        // Khởi tạo MessageService với callback khi có tin nhắn mới
        this.messageService = new MessageService(this::displayMessage);
        
        // Khởi tạo MessageServer để nhận kết nối từ người khác
        this.messageServer = new MessageServer(myPort, this::displayMessage);
    }

    private void startMessageServer() {
        messageServer.startServer();
        addSystemMessage("📡 Server chat đang chạy trên port " + myPort);
    }

    private void setupUserList() {
        // Danh sách peer mẫu để test
        String[] availableUsers = {
            "UserB - 127.0.0.1:12346",
            "UserC - 127.0.0.1:12347"
        };
        userListView.getItems().addAll(availableUsers);
    }

    private void setupEventHandlers() {
        // Xử lý chọn peer từ danh sách
        userListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    connectToPeer(newValue);
                }
            }
        );

        // Nút info panel
        infoButton.setOnAction(e -> toggleInfoPane());
    }

    // 🆕 CẬP NHẬT: KẾT NỐI ĐẾN PEER + LOAD LỊCH SỬ
    private void connectToPeer(String peerInfo) {
        try {
            String[] parts = peerInfo.split(" - ");
            String peerName = parts[0];
            String address = parts[1];
            String ip = address.split(":")[0];
            int port = Integer.parseInt(address.split(":")[1]);
            
            currentChatUser = peerName;
            chatName.setText(peerName);
            chatStatus.setText("Đang kết nối...");
            
            // 🆕 TẠO PEER KEY VÀ LOAD LỊCH SỬ
            currentPeerKey = ChatHistoryService.createPeerKey(peerName, ip, port);
            loadChatHistory(currentPeerKey);
            
            // Kết nối đến peer qua MessageService
            boolean success = messageService.connectToPeer(ip, port);
            
            if (success) {
                chatStatus.setText("Đã kết nối");
                addSystemMessage("✅ Đã kết nối với " + peerName);
            } else {
                chatStatus.setText("Lỗi kết nối");
                addSystemMessage("❌ Không thể kết nối với " + peerName);
            }
            
        } catch (Exception e) {
            chatStatus.setText("Lỗi kết nối");
            addSystemMessage("❌ Lỗi kết nối: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🆕 LOAD LỊCH SỬ CHAT TỪ FILE JSON
    private void loadChatHistory(String peerKey) {
        System.out.println("📂 Đang load lịch sử chat cho: " + peerKey);
        
        // Xóa tin nhắn hiện tại trên UI
        messageContainer.getChildren().clear();
        
        // Load lịch sử từ service
        List<ChatMessage> history = chatHistoryService.getChatHistory(peerKey);
        
        // Hiển thị lịch sử lên UI
        for (ChatMessage message : history) {
            addMessageToUI(message);
        }
        
        if (!history.isEmpty()) {
            addSystemMessage("📚 Đã tải " + history.size() + " tin nhắn từ lịch sử");
            System.out.println("✅ Đã load " + history.size() + " tin nhắn từ lịch sử");
        } else {
            System.out.println("ℹ️ Chưa có lịch sử chat với peer này");
        }
    }

    // GỬI TIN NHẮN KHI BẤM NÚT
    @FXML
    public void onSendMessage() {
        String msg = messageInput.getText().trim();
        if (msg.isEmpty()) return;

        messageService.sendMessage(msg);
        messageInput.clear();
    }

    // GỬI TIN NHẮN KHI NHẤN ENTER
    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onSendMessage();
        }
    }

    // 🆕 CẬP NHẬT: HIỂN THỊ TIN NHẮN + LUU VÀO LỊCH SỬ
    private void displayMessage(ChatMessage chatMessage) {
        System.out.println("💬 Đang xử lý tin nhắn: " + chatMessage.getContent());
        
        // 🆕 LUU TIN NHẮN VÀO LỊCH SỬ
        if (currentPeerKey != null) {
            chatHistoryService.addMessage(currentPeerKey, chatMessage);
            System.out.println("💾 Đã lưu tin nhắn vào lịch sử");
        } else {
            System.out.println("⚠️ Chưa có peer key, không lưu được lịch sử");
        }
        
        // HIỂN THỊ LÊN UI
        addMessageToUI(chatMessage);
    }

    // 🆕 TÁCH PHẦN HIỂN THỊ UI RA METHOD RIÊNG
    private void addMessageToUI(ChatMessage chatMessage) {
        HBox messageBox = new HBox();
        messageBox.setMaxWidth(Double.MAX_VALUE);
        messageBox.setPadding(new Insets(5, 10, 5, 10));
        
        // Tạo label với nội dung tin nhắn
        Label label = new Label(chatMessage.getContent());
        label.setWrapText(true);
        label.setMaxWidth(400);
        
        // Tạo bubble container
        HBox bubbleContainer = new HBox(label);
        bubbleContainer.setMaxWidth(400);
        
        // Áp dụng style class cho bubble và text
        if (chatMessage.isSelf()) {
            // Tin nhắn của mình
            bubbleContainer.getStyleClass().add("message-bubble-self");
            label.getStyleClass().add("message-text-self");
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(bubbleContainer, new Insets(0, 0, 0, 50));
        } else {
            // Tin nhắn từ người khác
            bubbleContainer.getStyleClass().add("message-bubble-other");
            label.getStyleClass().add("message-text-other");
            messageBox.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(bubbleContainer, new Insets(0, 50, 0, 0));
        }

        messageBox.getChildren().add(bubbleContainer);
        messageContainer.getChildren().add(messageBox);

        // Tự động scroll xuống tin nhắn mới
        chatScrollPane.applyCss();
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1.0);
    }

    // HIỂN THỊ TIN NHẮN HỆ THỐNG
    private void addSystemMessage(String content) {
        HBox messageBox = new HBox();
        messageBox.setMaxWidth(Double.MAX_VALUE);
        messageBox.setPadding(new Insets(5, 10, 5, 10));
        messageBox.setAlignment(Pos.CENTER);
        
        Label label = new Label(content);
        label.getStyleClass().add("system-message");
        label.setWrapText(true);
        label.setMaxWidth(400);
        
        messageBox.getChildren().add(label);
        messageContainer.getChildren().add(messageBox);

        // Tự động scroll
        chatScrollPane.applyCss();
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1.0);
    }

    // ẨN/HIỆN INFO PANEL (GIỮ NGUYÊN)
    private void toggleInfoPane() {
        isInfoPaneVisible = !isInfoPaneVisible;
        
        if (isInfoPaneVisible) {
            infoPane.setManaged(true);
            infoPane.setVisible(true);
            
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), infoPane);
            slideIn.setFromX(190);
            slideIn.setToX(0);
            slideIn.play();
        } else {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), infoPane);
            slideOut.setFromX(0);
            slideOut.setToX(190);
            slideOut.setOnFinished(e -> {
                infoPane.setVisible(false);
                infoPane.setManaged(false);
            });
            slideOut.play();
        }
    }

    // 🆕 CẬP NHẬT: DỌN DẸP KHI ĐÓNG ỨNG DỤNG
    public void shutdown() {
        System.out.println("🛑 Đang tắt ứng dụng...");
        
        if (messageService != null) {
            messageService.disconnect();
            System.out.println("✅ Đã ngắt MessageService");
        }
        if (messageServer != null) {
            messageServer.stopServer();
            System.out.println("✅ Đã dừng MessageServer");
        }
        
        // 🆕 LƯU LẠI TẤT CẢ LỊCH SỬ (nếu cần)
        System.out.println("💾 Lịch sử chat đã được lưu tự động");
        
        System.out.println("🛑 Ứng dụng đã tắt hoàn toàn");
    }

    @FXML
    private void chooseFile(){

    }
}