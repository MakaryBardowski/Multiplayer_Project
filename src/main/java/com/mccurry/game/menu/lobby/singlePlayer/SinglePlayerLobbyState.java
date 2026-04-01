package com.mccurry.game.menu.lobby.singlePlayer;

import com.jme3.anim.*;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.constants.CameraConstants;
import com.mccurry.game.locale.LocalizedString;
import com.mccurry.game.menu.lobby.PlayerClassRegistry;
import com.mccurry.game.menu.lobby.singlePlayer.launcher.SinglePlayerGameLaunchParams;
import com.mccurry.game.menu.lobby.singlePlayer.launcher.SinglePlayerGameLauncher;
import com.mccurry.game.menu.lobby.model.SelectablePlayerClass;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.settings.model.ResolutionSettings;
import com.mccurry.game.shared.ui.MenuState;
import com.mccurry.game.shared.ui.MenuType;
import com.mccurry.game.shared.ui.util.FontUtil;
import com.mccurry.game.shared.ui.util.TextureUtil;
import com.mccurry.game.sound.event.PlayGlobalSoundRequestEvent;
import com.mccurry.game.sound.model.global.GlobalSoundEntry;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.mccurry.game.menu.lobby.singlePlayer.LocaleConstants.LOCALE_KEY_START_GAME;
import static com.mccurry.game.shared.ui.util.TextureUtil.toGrayscale;

public class SinglePlayerLobbyState extends MenuState {
    private AssetManager assetManager;

    private EventBus orchestrationBus;
    private Map<SelectablePlayerClass, Button> buttonsByClass = new HashMap<>();
    private Label classNameLabel;
    private Label classDescriptionLabel;
    private SelectablePlayerClass currentPickedClass;

    private final SinglePlayerGameLauncher launcher = new SinglePlayerGameLauncher();

    @Override
    public void createSceneOnce(SimpleApplication application, Node stateSceneNode) {
        // Create 20x20 quad
        var quad = new Quad(6f, 6f);
        var floor = new Geometry("Floor", quad);

        // Move so center is at (0,0,0)
        floor.setLocalTranslation(-3f, 0f, 3f);
        floor.rotate(-((float) Math.PI / 2f), 0f, 0f); // make it horizontal

        // Unlit material
        var mat = new Material(
                application.getAssetManager(),
                "Common/MatDefs/Misc/Unshaded.j3md"
        );

        mat.setColor("Color", ColorRGBA.Blue); // chosen color
        floor.setMaterial(mat);

        stateSceneNode.attachChild(floor);


        var armatureRootNode = (Node) application.getAssetManager().loadModel("Models/HumanArmature/HumanArmature.j3o");
        setupModelLight(armatureRootNode);
        stateSceneNode.attachChild(armatureRootNode);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.5f));
        armatureRootNode.addLight(al);

        var skinningControl = armatureRootNode.getChild("Armature").getControl(SkinningControl.class);
        var composer = armatureRootNode.getChild("Armature").getControl(AnimComposer.class);

        var armature = skinningControl.getArmature();

        var legsMask = new ArmatureMask();
        legsMask.addBones(armature, "Pelvis", "ThighR", "ThighL", "ShinR", "ShinL", "FootR", "FootL");

        var upperBodyMask = new ArmatureMask();
        upperBodyMask.addBones(armature,
                "Head",
                "Spine", "ArmL", "ArmR", "ForearmR", "ForearmL", "HandR", "HandL");

        composer.makeLayer("LowerBody", legsMask);
        composer.makeLayer("UpperBody", upperBodyMask);

        composer.setCurrentAction("Lower_Stand", "LowerBody");


//        composer.setCurrentAction("Upper_HoldRifle", "UpperBody");
        composer.setCurrentAction("Upper_AimRifle", "UpperBody");


//        armatureRootNode.getChild("Armature").addControl(new AbstractControl() {
//                                                             private float pitch = 0;
//
//                                                             boolean up = true;
//                                                             boolean down = false;
//
//                                                             @Override
//                                                             protected void controlUpdate(float deltaTime) {
//                                                                 var maxDownPitch = 80 * FastMath.DEG_TO_RAD;
//                                                                 var maxUpPitch = 70*FastMath.DEG_TO_RAD;
//                                                                 if (up) {
//                                                                     pitch += 90 * deltaTime * FastMath.DEG_TO_RAD;
//                                                                     if (pitch >= maxUpPitch) {
//                                                                         down = true;
//                                                                         up = false;
//                                                                         pitch = maxUpPitch;
//                                                                     }
//                                                                 }
//                                                                 if (down) {
//                                                                     pitch += -90 * deltaTime * FastMath.DEG_TO_RAD;
//                                                                     if (pitch <= -maxDownPitch) {
//                                                                         down = false;
//                                                                         up = true;
//                                                                         pitch = -maxDownPitch;
//                                                                     }
//                                                                 }
//
//                                                                 armatureRootNode.rotate(0, 0.2f * deltaTime, 0);
//                                                             }
//
//                                                             @Override
//                                                             protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
//                                                                 // todo: rece musza obracac sie TYLKO gdy celujesz (zadna inna animacja)
//
//                                                                 var head = armature.getJoint("Head");
//                                                                 var torso = armature.getJoint("Spine");
//                                                                 var upperArmR = armature.getJoint("ArmR");
//                                                                 var upperArmL = armature.getJoint("ArmL");
//
//
//                                                                 var torsoPitchFraction = 0.3f;
//                                                                 var headPitchFraction = 1f/3f;
//                                                                 var handsPitchFraction = 1 - torsoPitchFraction-0.1f; // because hands are attached to torso, the sum is = pitch instead of rotating both by pitch
//
//
//                                                                 if(pitch > 0*FastMath.DEG_TO_RAD){
//                                                                     torsoPitchFraction = 0.4f;
//                                                                     headPitchFraction = 0.3f;
//                                                                     handsPitchFraction = 1 - torsoPitchFraction-0.2f;
//                                                                 }
//
//
//                                                                 // rotate around heads local X axis
//                                                                 var headXAxis = head.getLocalRotation().mult(Vector3f.UNIT_X);
//                                                                 var handsPitchQuat = new Quaternion().fromAngleAxis(pitch*handsPitchFraction, headXAxis);
//                                                                 upperArmR.setLocalRotation(handsPitchQuat.mult(upperArmR.getLocalRotation()));
//                                                                 upperArmL.setLocalRotation(handsPitchQuat.mult(upperArmL.getLocalRotation()));
//
//                                                                 var headPitchQuat = new Quaternion().fromAngleAxis(pitch*headPitchFraction, headXAxis);
//                                                                 head.setLocalRotation(headPitchQuat.mult(head.getLocalRotation()));
//
//
//                                                                 var torsoXAxis = torso.getLocalRotation().mult(Vector3f.UNIT_X);
//                                                                 var torsoPitch = new Quaternion().fromAngleAxis(pitch *torsoPitchFraction, torsoXAxis);
//                                                                 torso.setLocalRotation(torsoPitch.mult(torso.getLocalRotation()));
//
//
//                                                                 // torso is parent of both upper arms and head so updates them too
//                                                                 torso.update();
//                                                             }
//                                                         }
//        );


        // wazne! skinning control musi byc PO controlu ktory ustawia rotacje kosci
        var armatureSpatial = armatureRootNode.getChild("Armature");
        armatureSpatial.removeControl(skinningControl);
        armatureSpatial.addControl(skinningControl);
        // wazne! skinning control musi byc PO controlu ktory ustawia rotacje kosci

//        Node shinR = skin.getAttachmentsNode("ShinR");
//        Node shinL = skin.getAttachmentsNode("ShinL");
//        Node thighR = skin.getAttachmentsNode("ThighR");
//        Node thighL = skin.getAttachmentsNode("ThighL");
//        Node spine = skin.getAttachmentsNode("Spine");
//        Node head = skin.getAttachmentsNode("Head");
//        Node forearmR = skin.getAttachmentsNode("ForearmR");
//        Node forearmL = skin.getAttachmentsNode("ForearmL");


//        shinR.attachChild(createUnshadedBoxNode());
//        shinL.attachChild(createUnshadedBoxNode());
//        thighR.attachChild(createUnshadedBoxNode());
//        thighL.attachChild(createUnshadedBoxNode());
//        spine.attachChild(createUnshadedBoxNode());
    }

    public Node createUnshadedBoxNode() {
        Box box2 = new Box(0.02f, 0.02f, 0.02f);
        Geometry red = new Geometry("Box", box2);
        Material mat2 = new Material(getApplication().getAssetManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        red.setMaterial(mat2);
        Node n = new Node();
        n.attachChild(red);
        return n;
    }

    public void setupModelLight(Node node) {
        for (Spatial c : node.getChildren()) {

            if (c != null) {
                if (c instanceof Geometry g) {
                    Material originalMaterial = g.getMaterial();

                    if (originalMaterial.getTextureParam("BaseColorMap") != null) {
                        Material newMaterial = new Material(getApplication().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
                        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
                        g.setMaterial(newMaterial);
                    }
                } else if (c instanceof Node n) {
                    setupModelLight(n);
                }
            }
        }
    }

    @Override
    public void createUiOnce(SimpleApplication application, Node stateGuiNode) {
        orchestrationBus = application.getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus();
        assetManager = application.getAssetManager();
        var classPanel = classPanel(assetManager);

        var classPanelX = 10;
        classPanel.setLocalTranslation(new Vector3f(classPanelX, 330, 1));


        var confirmButton = confirmButton(LOCALE_KEY_START_GAME, button -> {
            playClickSound();
            button.setEnabled(false);
            button.setText("Loading...");

            launcher.launch(
                    getStateManager(),
                    SinglePlayerGameLaunchParams.builder()
                            .playerClass(currentPickedClass)
                            .build()
            );
        }, true);

        var baseResolution = ResolutionSettings.R_640x360;
        var buttonSize = confirmButton.getPreferredSize();
        var buttonX = ((int) (baseResolution.getWidth() * 0.98f)) - buttonSize.getX();
        confirmButton.setLocalTranslation(new Vector3f(buttonX, 60, 1));

        stateGuiNode.attachChild(classPanel);
        stateGuiNode.attachChild(confirmButton);
    }

    private Container classPanel(AssetManager assetManager) {
        var container = new Container();
        container.setInsets(new Insets3f(0, 0, 0, 0));
        container.setBackground(null);


        var classSelectionContainer = new Container();
        classSelectionContainer.setBackground(
                new QuadBackgroundComponent(
                        TextureUtil.loadPixelPerfectTexture(assetManager, "Interface/GUI/Lobby/ClassPanel.png")
                )
        );
        classSelectionContainer.setPreferredSize(new Vector3f(137, 71, 1));

        var classGrid = new Container();
        classGrid.setBackground(null);
        classGrid.setLayout(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.None));
        classGrid.setInsets(new Insets3f(4, 3, 2, 3));
        classGrid.setPreferredSize(new Vector3f(131, 65, 1));

        classSelectionContainer.addChild(classGrid);

        int col = 0;
        int row = 0;


        Button initialClassButton = null;

        for (var clazz : PlayerClassRegistry.getSelectablePlayerClasses()) {

            var classButton = classIconOf(clazz, assetManager);

            if (initialClassButton == null) {
                initialClassButton = classButton;
            }

            Container wrapper = new Container();
            wrapper.setBackground(null);

            // 1px gap only between cells
            float top = (row > 0) ? 1f : 0f;
            float left = (col > 0) ? 1f : 0f;

            wrapper.setInsets(new Insets3f(top, left, 0, 0));
            wrapper.setPreferredSize(new Vector3f(classButton.getPreferredSize().x + left, classButton.getPreferredSize().y + top, 1));

            wrapper.addChild(classButton);
            classGrid.addChild(wrapper, col, row);

            col++;
            if (col > 3) {
                col = 0;
                row++;
            }
        }


        var classDetailsPanel = new Container(new BoxLayout(Axis.Y, FillMode.None));
        classDetailsPanel.setInsets(new Insets3f(0, 0, 0, 0));
        classDetailsPanel.setBackground(
                new QuadBackgroundComponent(
                        TextureUtil.loadPixelPerfectTexture(assetManager, "Interface/GUI/Lobby/ClassDetailsPanel.png")
                )
        );

        classNameLabel = new Label(null);
        classNameLabel.setBackground(new QuadBackgroundComponent(
                TextureUtil.loadPixelPerfectTexture(assetManager, "Interface/GUI/Settings/SettingsTabButton.png")
        ));
        classNameLabel.setPreferredSize(new Vector3f(90, 18, 1));
        classNameLabel.setInsets(new Insets3f(0, 23, 0, 24));
        classNameLabel.setTextVAlignment(VAlignment.Center);
        classNameLabel.setTextHAlignment(HAlignment.Center);

        var font = FontUtil.loadPixelPerfectFont(assetManager, "Interface/Fonts/settings/settings7px.fnt");
        classNameLabel.setFont(font);
        classNameLabel.setColor(ColorRGBA.White.clone());

        classDescriptionLabel = new Label(null);
        classDescriptionLabel.setInsets(new Insets3f(3, 3, 3, 3));
        classDescriptionLabel.setPreferredSize(new Vector3f(131, 180 - 18 - 6, 1));
        classDescriptionLabel.setFont(font);
        classDescriptionLabel.setColor(ColorRGBA.White.clone());

        classDetailsPanel.setPreferredSize(new Vector3f(137, 180, 1));
        classDetailsPanel.addChild(classNameLabel);
        classDetailsPanel.addChild(classDescriptionLabel);


        container.addChild(classSelectionContainer);
        container.addChild(classDetailsPanel);

        initialClassButton.click();

        return container;
    }

    private Button confirmButton(
            String localeKey,
            @Nullable Command<Button> onClick,
            boolean enabled
    ) {
        var button = new Button(LocalizedString.get(localeKey));
        button.setPreferredSize(new Vector3f(120, 36, 1));
        button.setUserData(METADATA_LOCALE_KEY, localeKey);
        button.setTextVAlignment(VAlignment.Center);
        button.setTextHAlignment(HAlignment.Center);
        button.setHighlightColor(ColorRGBA.Red.clone());
        button.setFocusColor(ColorRGBA.Red.clone());

        var font = assetManager.loadFont("Interface/Fonts/pixelFlat11.fnt");
        Texture fontTex = font.getPage(0).getTextureParam("ColorMap").getTextureValue();
        fontTex.setMagFilter(Texture.MagFilter.Nearest);
        fontTex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        button.setFont(font);
        button.setColor(ColorRGBA.White.clone());

        Command<Button> defaultClickCommand;

        if (onClick != null) {
            defaultClickCommand = source -> {
                playClickSound();
                onClick.execute(source);
            };
        } else {
            defaultClickCommand = source -> {
                playClickSound();
            };
        }

        button.addClickCommands(defaultClickCommand);


        var buttonTexture = assetManager.loadTexture("Interface/GUI/MainMenu/MainMenuButton.png");

        if (!enabled) {
            button.setColor(ColorRGBA.DarkGray);
            button.setEnabled(false);
            buttonTexture = toGrayscale(buttonTexture);
        }

        buttonTexture.setMagFilter(Texture.MagFilter.Nearest);
        buttonTexture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        var backgroundComponent = new QuadBackgroundComponent();
        backgroundComponent.setTexture(buttonTexture);
        button.setBackground(backgroundComponent);


        return button;
    }


    public Button classIconOf(SelectablePlayerClass playerClass, AssetManager assetManager) {
        var b = new Button("");
        buttonsByClass.put(playerClass, b);


        b.setInsets(new Insets3f(0, 0, 0, 0));
        b.setBackground(
                new QuadBackgroundComponent(
                        TextureUtil.loadPixelPerfectTexture(assetManager, playerClass.getIconPath()
                        )
                )
        );
        b.addClickCommands(
                clickedButton -> {
                    clickedButton.setBackground(
                            new QuadBackgroundComponent(
                                    TextureUtil.loadPixelPerfectTexture(assetManager, playerClass.getSelecedIconPath()
                                    )
                            )
                    );

                    currentPickedClass = playerClass;

                    classNameLabel.setText(playerClass.getName());
                    classDescriptionLabel.setText(playerClass.getDescription());

                    buttonsByClass.entrySet().forEach(entry -> {
                        var button1 = entry.getValue();
                        var buttonClass = entry.getKey();

                        if (button1 != clickedButton) {
                            button1.setBackground(
                                    new QuadBackgroundComponent(
                                            TextureUtil.loadPixelPerfectTexture(assetManager, buttonClass.getIconPath()
                                            )
                                    )
                            );
                        }
                    });

                }
        );
        b.setPreferredSize(new Vector3f(32, 32, 1));


        return b;
    }

    @Override
    protected void onEnable() {
        getApplication().getViewPort().setBackgroundColor(ColorRGBA.DarkGray);
        getApplication().getCamera().setLocation(new Vector3f(1, 2, -5)); // 0,1,-5
        getApplication().getCamera().lookAt(new Vector3f(0, 1, 10), Vector3f.UNIT_Y);
    }

    @Override
    protected void onDisable() {
        CameraConstants.applyDefaultTransform(getApplication().getCamera());
        getApplication().getViewPort().setBackgroundColor(ColorRGBA.Black);
    }

    private void playClickSound() {
        orchestrationBus.publish(new PlayGlobalSoundRequestEvent(GlobalSoundEntry.MENU_BUTTON_CLICK), null);
    }
}
