package com.mccurry.game.menu.lobby;

import com.mccurry.game.menu.lobby.model.SelectablePlayerClass;
import com.mccurry.game.util.string.JmeRgbaTagsUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerClassRegistry {
    private static final List<SelectablePlayerClass> PLAYER_CLASSES;

    static {

        var assault = SelectablePlayerClass.builder()
                .iconPath("Interface/GUI/Lobby/ClassIcons/Assault.png")
                .selecedIconPath("Interface/GUI/Lobby/ClassIcons/AssaultSelected.png")
                .name("Assault")
                .description(
                        "Ranged: %s\nMelee: %s\nHealth: %s\nUtility: %s".formatted(
                                        JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.BRIGHT_GREEN, "----", JmeRgbaTagsUtil.ColorTag.BLACK),
                                        JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.YELLOW, "--", JmeRgbaTagsUtil.ColorTag.BLACK),
                                        JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.YELLOW, "--", JmeRgbaTagsUtil.ColorTag.BLACK),
                                        JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.BRIGHT_RED, "-", JmeRgbaTagsUtil.ColorTag.BLACK)
                ))
                .build();
        var medic = SelectablePlayerClass.builder()
                .iconPath("Interface/GUI/Lobby/ClassIcons/Medic.png")
                .selecedIconPath("Interface/GUI/Lobby/ClassIcons/MedicSelected.png")
                .name("Medic")
                .description(
                        "Ranged: %s\nMelee: %s\nHealth: %s\nUtility: %s".formatted(
                                JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.YELLOW, "--", JmeRgbaTagsUtil.ColorTag.BLACK),
                                JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.YELLOW, "--", JmeRgbaTagsUtil.ColorTag.BLACK),
                                JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.BRIGHT_RED, "-", JmeRgbaTagsUtil.ColorTag.BLACK),
                                JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.BRIGHT_GREEN, "----", JmeRgbaTagsUtil.ColorTag.BLACK)
                        ))
                .build();
        var engineer = SelectablePlayerClass.builder()
                .iconPath("Interface/GUI/Lobby/ClassIcons/Engineer.png")
                .selecedIconPath("Interface/GUI/Lobby/ClassIcons/EngineerSelected.png")
                .name("Engineer")
                .description(
                        "Ranged: %s\nMelee: %s\nHealth: %s\nUtility: %s".formatted(
                                JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.BRIGHT_RED, "-", JmeRgbaTagsUtil.ColorTag.BLACK),
                                JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.BRIGHT_GREEN, "---", JmeRgbaTagsUtil.ColorTag.BLACK),
                                JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.YELLOW, "--", JmeRgbaTagsUtil.ColorTag.BLACK),
                                JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.BRIGHT_GREEN, "----", JmeRgbaTagsUtil.ColorTag.BLACK)
                        ))
                .build();
        var commando = SelectablePlayerClass.builder()
                .iconPath("Interface/GUI/Lobby/ClassIcons/Commando.png")
                .selecedIconPath("Interface/GUI/Lobby/ClassIcons/CommandoSelected.png")
                .name("Commando")
                .description(
                        "Ranged: %s\nMelee: %s\nHealth: %s\nUtility: %s".formatted(
                                JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.BRIGHT_GREEN, "---", JmeRgbaTagsUtil.ColorTag.BLACK),
                                JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.BRIGHT_GREEN, "---", JmeRgbaTagsUtil.ColorTag.BLACK),
                                JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.BRIGHT_RED, "-", JmeRgbaTagsUtil.ColorTag.BLACK),
                                JmeRgbaTagsUtil.colorToColor(JmeRgbaTagsUtil.ColorTag.YELLOW, "--", JmeRgbaTagsUtil.ColorTag.BLACK)
                        )
                )
                .build();

        PLAYER_CLASSES = List.of(
                assault, medic, engineer, commando
        );
    }

    public static List<SelectablePlayerClass> getSelectablePlayerClasses() {
        return new ArrayList<>(PLAYER_CLASSES);
    }
}
