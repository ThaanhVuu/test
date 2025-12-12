package dhkthn.p2p;

import dhkthn.p2p.config.AppConfig;
import dhkthn.p2p.model.PeerDiscovery;
import dhkthn.p2p.model.PeerFileTransfer;
import dhkthn.p2p.model.PeerInfo;
import dhkthn.p2p.model.PeerListener;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

class Main{
    private static final List<PeerInfo> peerOnlines = new CopyOnWriteArrayList<>();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        String host = "127.0.0.1";
        if(!AppConfig.getLocalIpAddress().isEmpty()) host = AppConfig.getLocalIpAddress();

        PeerInfo myInfo = PeerInfo.builder()
                .username("vu")
                .host(host)
                .port(AppConfig.getPort())
                .build();

        PeerListener listener = new PeerListener() {
            @Override
            public void onPeerFound(PeerInfo peer) {

            }

            @Override
            public void onFileReceived(File file, PeerInfo sender) {
                System.out.println("Received file: " + file.getAbsoluteFile());
            }

            @Override
            public void onError(String message) {
                System.out.println("[ERROR] : " + message);
            }

            @Override
            public void onInfo(String message) {
                System.out.println("[INFO] : " + message);
            }

            @Override
            public void onPeersListUpdated(Set<PeerInfo> peers) {
                peerOnlines.clear();
                peerOnlines.addAll(peers);
            }
        };

        ExecutorService pool = AppConfig.getPool();

        PeerDiscovery discovery = new PeerDiscovery(myInfo, pool, listener);
        PeerFileTransfer transfer = new PeerFileTransfer(myInfo, pool, listener);

        discovery.start();
        transfer.startServer();
        while (true){
            printMainMenu();
            cliAction(transfer);
        }

    }

    private static void printMainMenu() {
        System.out.println("\n================ MENU ================");
        System.out.println("Number Peer online: " + peerOnlines.size());
        System.out.println("1. List peer online");
        System.out.println("2. Send file to peer");
        System.out.println("0. Exit");
        System.out.print(">> Your choice: ");
    }

    private static void cliAction(PeerFileTransfer fileTransfer){
        int choice = sc.nextInt();
        sc.nextLine();
        switch (choice){
            case 1: {
                System.out.println("================Peers=======================");
                printPeer();
                System.out.println("============================================");
                break;
            }
            case 2:{
                System.out.println("Enter id of peer to send file: ");
                PeerInfo peerInfo = peerOnlines.get(sc.nextInt());
                sc.nextLine();
                System.out.println("enter file path");
                String filePath = sc.nextLine();
                File file = new File(filePath);
                fileTransfer.sendFile(peerInfo, file);
                break;
            }
            case 3:{
                System.exit(0);
                break;
            }
            default:{
                System.out.println("wrong choice, do again!");
            }
        }
    }

    private static void printPeer(){
        for (int i = 0 ; i < peerOnlines.size(); i++){
            PeerInfo peerInfo = peerOnlines.get(i);
            System.out.println(i + "." + peerInfo);
        }
    }
}