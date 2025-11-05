package dirty.client;

import dirty.Utils.Ansi;
import dirty.client.appStates.ClientGameAppState;
import dirty.client.appStates.LobbyTeamViewAppState;
import dirty.client.appStates.MainMenuAppState;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import dirty.server.ServerGameAppState;

public class MainMenuController implements ScreenController {
    private static ServerGameAppState serverGameAppState;
    @Override
    public void bind(Nifty nifty, Screen screen) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onStartScreen() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onEndScreen() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void startServerAndJoinLobby() {
        goToLobbyScreenHost();

        Main instance = Main.getInstance();
        serverGameAppState = new ServerGameAppState(instance.getAssetManager(), instance.getRenderManager());
        instance.getStateManager().attach(serverGameAppState);

        var clientGameState = new ClientGameAppState(Main.getInstance(), "localhost");
        instance.getStateManager().attach(clientGameState);
        MainMenuAppState.setClient(clientGameState);

        var teamViewer = new LobbyTeamViewAppState();
        instance.getStateManager().attach(teamViewer);



        System.out.println(Ansi.withAnsiColor("server started.", Ansi.BRIGHT_YELLOW));
    }

    public static void joinLobby() {

        MainMenuController.goToLobbyScreenClient();

//        System.out.println("joining lobby "+Main.getInstance().getCamera().getRotation());
//        Main.getInstance().getCamera().setLocation(new Vector3f(0,0,0));

        Element textFieldElement = MainMenuAppState.getNifty().getCurrentScreen().findElementById("ip-textfield");
        TextField textFieldControl = textFieldElement.getNiftyControl(TextField.class);
        String serverIpAddress = textFieldControl.getDisplayedText();

        Main instance = Main.getInstance();
        var clientGameState = new ClientGameAppState(Main.getInstance(), serverIpAddress);
        instance.getStateManager().attach(clientGameState);
        MainMenuAppState.setClient(clientGameState);

        var teamViewer = new LobbyTeamViewAppState();
        instance.getStateManager().attach(teamViewer);
    }

    public static void createLobby() {
        startServerAndJoinLobby();
    }

    public static void goToLobbyScreenClient() {
        var nifty = MainMenuAppState.getNifty();
        nifty.gotoScreen("LobbyClient");
    }

    public static void goToLobbyScreenHost() {
        var nifty = MainMenuAppState.getNifty();
        nifty.gotoScreen("LobbyHost");
    }

    // LOBBY
    public static String getClassPanelWidth() {
        var nifty = MainMenuAppState.getNifty();
        int screenHeight = nifty.getRenderEngine().getHeight();

        int buttonHeight = (int) (screenHeight * 0.075);
        return buttonHeight * 3 + "px";
    }

    public static String getClassPanelHeight() {
        var nifty = MainMenuAppState.getNifty();
        int screenHeight = nifty.getRenderEngine().getHeight();

        int buttonHeight = (int) (screenHeight * 0.075);
        return buttonHeight + "px";
    }

    public static void startGame() {
        serverGameAppState.startGame();
    }

    public static void leaveLobby() {
        Main instance = Main.getInstance();

        MainMenuAppState m = instance.getStateManager().getState(MainMenuAppState.class);
        instance.getStateManager().detach(m);
    }

    public static void chooseAssault() {
        LobbyTeamViewAppState.changeClass(0);
        System.out.println(Ansi.withAnsiColor("chosen assault.",Ansi.BRIGHT_BLUE));
    }

    public static void chooseMedic() {
        LobbyTeamViewAppState.changeClass(1);

        System.out.println(Ansi.withAnsiColor("chosen medic.",Ansi.BRIGHT_BLUE));
    }

    public static void chooseCombatEngineer() {
        LobbyTeamViewAppState.changeClass(2);
        System.out.println(Ansi.withAnsiColor("chosen combat engineer.",Ansi.BRIGHT_BLUE));
    }
}
