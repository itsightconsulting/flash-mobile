package isdigital.veridium.flash.constants;

public enum Profiles {
    SOCIO(1);

    final int code;

    Profiles(int code) {
        this.code = code;
    }

    public int get() {
        return code;
    }
}
