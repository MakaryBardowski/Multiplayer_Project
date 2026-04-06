package com.mccurry.game.gameplay.system.modelhierarchy.component;

import com.jme3.anim.SkinningControl;
import com.jme3.scene.Node;
import com.mccurry.game.gameplay.core.ecs.component.Component;

public class HumanoidModelComponent implements Component {

    private final SkinningControl skinningControl;

    public HumanoidModelComponent(Node humanoidRootNode){
        this.skinningControl = humanoidRootNode.getChild("Armature").getControl(SkinningControl.class);
    }

    public Node getRightHandAttachmentNode(){
        return skinningControl.getAttachmentsNode("HandR");
    }

    public Node getLeftHandAttachmentNode(){
        return skinningControl.getAttachmentsNode("HandL");
    }

    public Node getHeadAttachmentNode(){
        return skinningControl.getAttachmentsNode("Head");
    }
}
