package Main;

import ChessField.ChessFieldHelper;
import ChessField.ChessFieldPane;
import Pieces.Color;
import Player.DummyPlayer;
import Player.HumanPlayer;
import Player.Player;
import Utils.JARClassLoader;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.stream.Collectors;

public class ApplicationViewHolder extends BorderPane {
    private final MainActivity mainActivity;
    private ChessFieldHelper referee;

    private BorderPane topPane;

    private MenuBar menuBar;
    private Menu menuGame;
    private MenuItem menuItemNewGame;
    private MenuItem menuItemPauseGame;
    private MenuItem menuItemNewWindow;
    private MenuItem menuItemQuit;
    private Menu menuPlayerA;
    private Menu menuPlayerB;
    private HashMap<RadioMenuItem, Class> playerTypesPlayerA;
    private HashMap<RadioMenuItem, Class> playerTypesPlayerB;

    private ToolBar toolBar;
    private Button toolBarBtnNewGame;
    private Button toolBarBtnPauseGame;
    private Button toolBarBtnQuit;

    private ChessFieldPane paneGame;
    private BorderPane paneGameFrame;
    private Label lblPlayerA;
    private Label lblPlayerB;

    private Label lblInfo;

    public ApplicationViewHolder(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        initializeTop();
        initializeBoard();
        initializeInfo();
    }

    private void initializeInfo() {
        lblInfo = new Label("(Info-Bereich)");
        lblInfo.setPadding(new Insets(10));
        this.setBottom(lblInfo);
    }

    private void initializeTop() {
        topPane = new BorderPane();
        this.setTop(topPane);

        initializeMenuBar();
        initializeToolbar();
    }

    private void initializeBoard() {
        paneGameFrame = new BorderPane();

        lblPlayerA = new Label("Spieler A");
        lblPlayerB = new Label("Spieler B");

        paneGameFrame.setTop(lblPlayerA);
        paneGameFrame.setAlignment(lblPlayerA, Pos.CENTER);
        paneGameFrame.setBottom(lblPlayerB);
        paneGameFrame.setAlignment(lblPlayerB, Pos.CENTER);

        paneGame = new ChessFieldPane();

        paneGameFrame.setCenter(paneGame);

        ScrollPane paneGameFrameScrollContainer = new ScrollPane(paneGameFrame);
        paneGameFrameScrollContainer.setFitToWidth(true);
        paneGameFrameScrollContainer.setFitToHeight(true);

        this.setCenter(paneGameFrameScrollContainer);
    }

    private void initializeToolbar() {
        toolBarBtnNewGame = new Button();
        toolBarBtnNewGame.setTooltip(new Tooltip("Neues Spiel starten"));
        toolBarBtnNewGame.setOnAction(e -> referee = newGameAction());
        toolBarBtnPauseGame = new Button();
        toolBarBtnPauseGame.setTooltip(new Tooltip("Spiel pausieren"));
        toolBarBtnPauseGame.setOnAction(e -> mainActivity.pauseGameAction(referee, this));
        toolBarBtnPauseGame.setDisable(true);
        toolBarBtnQuit = new Button();
        toolBarBtnQuit.setTooltip(new Tooltip("Fenster schließen"));
        toolBarBtnQuit.setOnAction(e -> mainActivity.quitAction());

        ImageView imageViewNewGame = new ImageView(
                new Image(getClass().getClassLoader().getResource("res/icons/icon_new_game.png").toExternalForm()));
        imageViewNewGame.setFitHeight(25);
        imageViewNewGame.setFitWidth(25);
        toolBarBtnNewGame.setGraphic(imageViewNewGame);

        ImageView imageViewPauseGame = new ImageView(
                new Image(getClass().getClassLoader().getResource("res/icons/icon_pause.png").toExternalForm()));
        imageViewPauseGame.setFitHeight(25);
        imageViewPauseGame.setFitWidth(25);
        toolBarBtnPauseGame.setGraphic(imageViewPauseGame);

        ImageView imageViewQuit = new ImageView(
                new Image(getClass().getClassLoader().getResource("res/icons/icon_quit.png").toExternalForm()));
        imageViewQuit.setFitHeight(25);
        imageViewQuit.setFitWidth(25);
        toolBarBtnQuit.setGraphic(imageViewQuit);

        toolBar = new ToolBar(toolBarBtnNewGame, toolBarBtnPauseGame, toolBarBtnQuit);

        topPane.setBottom(toolBar);
    }

    private void initializeMenuBar() {
        menuGame = new Menu("Spiel");
        menuItemNewGame = new MenuItem("Neues Spiel");
        menuItemNewGame.setMnemonicParsing(true);
        menuItemNewGame.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        menuItemNewGame.setOnAction(e -> referee = newGameAction());
        menuItemPauseGame = new MenuItem("Spiel pausieren");
        menuItemPauseGame.setOnAction(event -> mainActivity.pauseGameAction(referee, this));
        menuItemPauseGame.setDisable(true);
        menuItemNewWindow = new MenuItem("Neues Fenster");
        menuItemNewWindow.setOnAction(e -> mainActivity.newWindowAction(new Stage()));
        menuItemQuit = new MenuItem("Beenden");
        menuItemQuit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        menuItemQuit.setOnAction(e -> mainActivity.quitAction());
        menuGame.getItems().addAll(menuItemNewGame, menuItemPauseGame, menuItemNewWindow, menuItemQuit);

        menuPlayerA = new Menu("Spieler A");
        playerTypesPlayerA = loadPlayerTypes(playerTypesPlayerA, menuPlayerA);
        menuPlayerB = new Menu("Spieler B");
        playerTypesPlayerB = loadPlayerTypes(playerTypesPlayerB, menuPlayerB);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(menuGame, menuPlayerA, menuPlayerB);

        topPane.setTop(menuBar);
    }

    private ChessFieldHelper newGameAction() {
        Player playerBlack = getPlayerOfSelectedType(menuPlayerA, playerTypesPlayerA);
        playerBlack.initialize(paneGame, "Schwarz", Color.BLACK);
        Player playerWhite = getPlayerOfSelectedType(menuPlayerB, playerTypesPlayerB);
        playerWhite.initialize(paneGame, "Weiß", Color.WHITE);

        return mainActivity.newGameAction(paneGame, this, playerBlack, playerWhite);
    }

    private Player getPlayerOfSelectedType(Menu menu, HashMap<RadioMenuItem, Class> map) {
        for (MenuItem item : menu.getItems()) {
            if (((RadioMenuItem) item).isSelected()) {
                try {
                    return (Player) map.get(item).newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private HashMap<RadioMenuItem, Class> loadPlayerTypes(HashMap<RadioMenuItem, Class> map, Menu menu) {
        map = new HashMap<>();

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioMenuItem humanPlayer = new RadioMenuItem("Mensch");
        humanPlayer.setSelected(true);
        humanPlayer.setToggleGroup(toggleGroup);
        map.put(humanPlayer, HumanPlayer.class);

        RadioMenuItem dummyPlayer = new RadioMenuItem("Built-In Dummy");
        dummyPlayer.setToggleGroup(toggleGroup);
        map.put(dummyPlayer, DummyPlayer.class);

        for (File file : (new File("bots")).listFiles()) {
            RadioMenuItem jarPlayer = new RadioMenuItem(file.getName());//getManifestEntry(file, "Player"));
            jarPlayer.setToggleGroup(toggleGroup);
            map.put(jarPlayer, getClassFromJar(file));
        }

        Set<RadioMenuItem> radioMenuItems = map.keySet();
        List<RadioMenuItem> sortedRadioMenuItems = radioMenuItems.stream()
                .sorted(Comparator.comparing(MenuItem::getText))
                .collect(Collectors.toList());
        menu.getItems().addAll(sortedRadioMenuItems);

        return map;
    }

    public Class getClassFromJar(File file) {
        try {
            JARClassLoader classLoader = new JARClassLoader(file.getAbsolutePath());

            return classLoader.loadClass(getManifestEntry(file, "Player"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getManifestEntry(File file, String tag) {
        try {
            URL fileURL = new URL("file:" + file.getAbsolutePath());
            URL u = new URL("jar", "", fileURL + "!/");
            JarURLConnection uc = (JarURLConnection) u.openConnection();
            Attributes attr = uc.getMainAttributes();
            return attr != null ? attr.getValue(tag) : null;
        } catch (Exception exc) {
            return null;
        }
    }

    public void gameEndCallback(Player winner) {
        Platform.runLater(() -> {
            lblInfo.setText(winner.getDisplayElement() + " hat gewonnen.");
            onGameStartPause();
        });
    }

    public void onGameStartPause() {
        toolBarBtnNewGame.setDisable(toolBarBtnPauseGame.isDisable());
        menuItemNewGame.setDisable(menuItemPauseGame.isDisable());

        toolBarBtnPauseGame.setDisable(!toolBarBtnPauseGame.isDisable());
        menuItemPauseGame.setDisable(!menuItemPauseGame.isDisable());
    }
}
