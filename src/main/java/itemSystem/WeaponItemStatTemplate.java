package itemSystem;

import com.jme3.math.Vector3f;
import lombok.Getter;

public class WeaponItemStatTemplate {

    @Getter
    public static class WeaponStatTemplate {

        private final float damage;
        private final float attacksPerSecond;

        public WeaponStatTemplate(float damage, float attacksPerSecond) {
            this.damage = damage;
            this.attacksPerSecond = attacksPerSecond;
        }
    }

    @Getter
    public static class MeleeWeaponStatTemplate extends WeaponStatTemplate {

        public static MeleeWeaponStatTemplate AXE_DEFAULT_STATS = new MeleeWeaponStatTemplate(8f,1.25f);
        public static MeleeWeaponStatTemplate KNIFE_DEFAULT_STATS = new MeleeWeaponStatTemplate(6f,2f);

        public MeleeWeaponStatTemplate(float damage, float attacksPerSecond) {
            super(damage, attacksPerSecond);
        }
    }

    @Getter
    public static class RangedWeaponStatTemplate extends WeaponStatTemplate {
        public static RangedWeaponStatTemplate RIFLE_MANNLICHER_95_DEFAULT_STATS = new RangedWeaponStatTemplate(7f,1.5f,8,
                20,
                5, 0.55f, .25f,
                new Vector3f(0,0,3),
                new Vector3f(-0.1f, .0f, .00f),
                0.3f
        );
        public static RangedWeaponStatTemplate PISTOL_C96_DEFAULT_STATS = new RangedWeaponStatTemplate(4.9f,4.43f,10,
                0,
                0,0,0,
                new Vector3f(0,0,0),
                new Vector3f(0,0,0),
                0
        );
        public static RangedWeaponStatTemplate RIFLE_BORYSOV_DEFAULT_STATS = new RangedWeaponStatTemplate(3.9f,6f,3024,
                40,
                1.4f, 0.85f, .35f,
                new Vector3f(0,0.7f,3),
                new Vector3f(-0.f, .0f, .00f),
                -0.1f
        );
        public static RangedWeaponStatTemplate LMG_HOTCHKISS_DEFAULT_STATS = new RangedWeaponStatTemplate(4.4f,7.5f,75,
                0,
                0,0,0,
                new Vector3f(0,0,0),
                new Vector3f(0,0,0),
                0
        );

        private final int maxAmmo;


        // recoil
        private final int cameraRecoilSnapiness;

        private final float cameraVerticalRecoil;
        private final float cameraHorizontalRecoil;
        private final float cameraRollRecoil;

        private final float cameraBackRecoil;

        private final Vector3f gunRecoilMaxPosition;
        private final Vector3f gunRecoilMaxRotation;

        public RangedWeaponStatTemplate(float damage,
                                        float attacksPerSecond,
                                        int maxAmmo,
                                        int recoilSnapiness,
                                        float cameraVerticalRecoil,
                                        float cameraHorizontalRecoil,
                                        float cameraRollRecoil,
                                        Vector3f gunRecoilMaxPosition,
                                        Vector3f gunRecoilMaxRotation,
                                        float cameraBackRecoil
        ) {
            super(damage, attacksPerSecond);
            this.maxAmmo = maxAmmo;
            this.cameraRecoilSnapiness = recoilSnapiness;
            this.cameraVerticalRecoil = cameraVerticalRecoil;
            this.cameraHorizontalRecoil = cameraHorizontalRecoil;
            this.cameraRollRecoil = cameraRollRecoil;
            this.gunRecoilMaxPosition = gunRecoilMaxPosition;
            this.gunRecoilMaxRotation = gunRecoilMaxRotation;
            this.cameraBackRecoil = cameraBackRecoil;
        }
    }
}
