package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Client;
import com.mygdx.game.DarwinsDuel;
import com.mygdx.game.entities.*;
import com.mygdx.game.listeners.EventListener;
import com.mygdx.global.*;
import com.badlogic.gdx.Screen;
import com.mygdx.server.UUIDSerializer;


import java.io.IOException;
import java.util.UUID;

public class LoginScreen implements Screen {
    private DarwinsDuel gameObj;

    private final Stage stage;
    Drawable background = new TextureRegionDrawable(new Texture(Gdx.files.internal("mainscreen.png")));
    final Skin skin = new Skin(Gdx.files.internal("buttons/uiskin.json"));

    private final Table loginTable = new Table();
    private final Table signupTable = new Table();
    private final Table bgTable = new Table();

    // UI for login
    private Label loginLabel;
    private TextButton loginButton;
    private TextField usernameLField;
    private TextField passwordLField;
    private TextButton changeToSignUp;

    // UI for sign up
    private Label signUpLabel;
    private TextButton signUpButton;
    private TextField usernameSField;
    private TextField passwordSField;
    private TextButton changeToLogin;

    private Label errorLabel;
    boolean login = false;
    boolean signUp = false;
    boolean joined = false;

    /*
    stuff for firebase login (to be refactored):
     */


    public LoginScreen(DarwinsDuel gameObj) {

        System.out.println("LoginScreen created");
        stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        initialiseBgTable();
        initialiseLoginTable();
        initialiseSignUpTable();

        Stack stack = new Stack();
        stack.setFillParent(true);
        stack.add(bgTable);
        stack.add(loginTable);
        stack.add(signupTable);


        stage.addActor(stack);
        //stage.addActor(signupTable);
//        stage.setDebugAll(true);
    }

    public void initialiseBgTable() {
        bgTable.setFillParent(true);
        bgTable.setBackground(background);
    }

    public void initialiseLoginTable() {
        // login table
        loginTable.setFillParent(true);

        loginLabel = new Label("Login", skin);

        usernameLField = new TextField("", skin);
        usernameLField.setMessageText("Username");

        passwordLField = new TextField("", skin);
        passwordLField.setMessageText("Password");
        passwordLField.setPasswordCharacter('*');
        passwordLField.setPasswordMode(true);

        loginButton = new TextButton("Log In", skin);
        loginButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // connect to Firebase and get player info
                login = true;

                return super.touchDown(event, x, y, pointer, button);
                //create client, connect client to server. start battle
            }
        });

        changeToSignUp = new TextButton("Sign up", skin);
        changeToSignUp.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // change to sign up interface
                loginTable.setVisible(false);
                signupTable.setVisible(true);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        loginTable.clear();
        loginTable.add(loginLabel).colspan(3).row();
        loginTable.add(usernameLField).width(250).padTop(50).colspan(3).row();
        loginTable.add(passwordLField).width(250).padTop(10).colspan(3).row();
        loginTable.add().uniform().padTop(100);
        loginTable.add(loginButton).uniform();
        loginTable.add(changeToSignUp).uniform().right();
        loginTable.setVisible(true);
    }

    public void initialiseSignUpTable() {
        // sign up table
        signupTable.setFillParent(true);

        signUpLabel = new Label("Sign Up", skin);

        usernameSField = new TextField("", skin);
        usernameSField.setMessageText("Username");

        passwordSField = new TextField("", skin);
        passwordSField.setMessageText("Password");
        passwordSField.setPasswordCharacter('*');
        passwordSField.setPasswordMode(true);

        signUpButton = new TextButton("Sign Up", skin);
        signUpButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // create new player, connect to Firebase, and upload new player info

                return super.touchDown(event, x, y, pointer, button);
                //create client, connect client to server. start battle

            }
        });
        changeToLogin = new TextButton("Login", skin);
        changeToLogin.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // change to login interface
                loginTable.setVisible(true);
                signupTable.setVisible(false);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        signupTable.clear();
        signupTable.add(signUpLabel).colspan(3).row();
        signupTable.add(usernameSField).width(250).padTop(50).colspan(3).row();
        signupTable.add(passwordSField).width(250).padTop(10).colspan(3).row();
        signupTable.add().uniform().padTop(100);
        signupTable.add(signUpButton).uniform();
        signupTable.add(changeToLogin).uniform().right();
        signupTable.setVisible(false);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1); // Clear to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the color buffer
        //System.out.println("currently rendering BattleScreen");
        stage.act(delta);
        stage.draw();

        if (login && !joined) {
            joined = true;
            DarwinsDuel.gameState = DarwinsDuel.GameState.FREEROAM;

            DarwinsDuel.client = new Client();
            Client myClient = DarwinsDuel.getClient();


            myClient.addListener(new EventListener());
            //client.addListener(new ConnectionStateListener());

            myClient.getKryo().register(UUID.class,  new UUIDSerializer());

            myClient.getKryo().register(AddPlayerEvent.class);
            myClient.getKryo().register(AttackEvent.class);
            myClient.getKryo().register(BattleState.class);
            myClient.getKryo().register(EndBattleEvent.class);
            myClient.getKryo().register(JoinRequestEvent.class);
            myClient.getKryo().register(JoinResponseEvent.class);
            myClient.getKryo().register(StartBattleEvent.class);
            myClient.getKryo().register(ChangePetEvent.class);
            myClient.getKryo().register(java.util.UUID.class);
            myClient.getKryo().register(java.util.ArrayList.class);

            myClient.getKryo().register(Player.class);
            myClient.getKryo().register(Player.Pet.class);
            myClient.getKryo().register(Entity.class);
            myClient.getKryo().register(MeowmadAli.class);
            myClient.getKryo().register(CrocLesnar.class);
            myClient.getKryo().register(Froggy.class);
            myClient.getKryo().register(BadLogic.class);
            myClient.getKryo().register(Creature.class);
            myClient.getKryo().register(Creature[].class);
            myClient.getKryo().register(Skill.class);
            myClient.getKryo().register(Skill[].class);
            myClient.getKryo().register(BattleState.Turn.class);
            myClient.getKryo().register(TextImageButton.class);

            //start the client
            myClient.start();

            // Connect to server
            // Connect to the server in a separate thread
            Thread connectThread = getThread(myClient);
            //client.sendTCP(new JoinRequestEvent(new Player(5, 5)));

            try {
                connectThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AddPlayerEvent addPlayerEvent = new AddPlayerEvent();
            addPlayerEvent.username = usernameLField.getText();
            myClient.sendTCP(addPlayerEvent);
        }
    }

    private Thread getThread(Client client) {
        Thread connectThread = new Thread(() -> {
            String host = "localhost"; // Server's IP address if not running locally
            int tcpPort = 55555;       // Must match the server's TCP port
            int udpPort = 55577;       // Must match the server's UDP port

            try {
                client.connect(5000, host, tcpPort, udpPort);
                System.out.println("Connected to the server.");

                JoinRequestEvent joinRequestEvent = new JoinRequestEvent();
                client.sendTCP(joinRequestEvent);
                System.out.println("JoinRequestEvent sent");

            } catch (IOException e) {
                System.err.println("Error connecting to the server: " + e.getMessage());
                e.printStackTrace();
                //errorLabel.setText(e.getMessage());
            }
        });
        connectThread.start(); // Start the thread
        return connectThread;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
