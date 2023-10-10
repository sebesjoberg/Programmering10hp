import java.io.Serializable;

class InitialMessage implements Serializable {
    final private boolean fogOfWar;
    private String version;

    String getVersion() {
        return version;
    }

    InitialMessage(Boolean fogOfWar, String version) {
        this.fogOfWar = fogOfWar;
        this.version = version;
    }

    boolean checkFogOfWar(InitialMessage message) {
        return this.fogOfWar == message.fogOfWar;
    }

    boolean checkVersion (InitialMessage message) {
        return this.version.equals(message.version);
    }

}
