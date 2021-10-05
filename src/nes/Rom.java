package nes;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class Rom {
    public final static int HORIZONTAL = 0;
    public final static int VERTICAL = 1;

    private int prgPageCnt, chrPageCnt;
    private int mirroring;
    private boolean sramEnable, trainerEnable;
    private boolean fourScreen;
    private int mapper;
    private byte[] romDat, chrDat, sram, vram;

    public Rom(Nes n) {
    }

    public void reset() {

    }

    public void release() {
        romDat = null;
        chrDat = null;
        sram = null;
        vram = null;
    }

    public void load(String fname) throws IOException {
        release();

        FileConnection fileConnection = (FileConnection) Connector.open(fname);
        InputStream is = fileConnection.openInputStream ();
        byte[] dat = new byte[is.available()];
        is.read(dat, 0, dat.length);

        if (!(dat[0] == 'N' && dat[1] == 'E' && dat[2] == 'S' && dat[3] == '\u001A'))
            throw new IOException("rom signature is invalid");

        prgPageCnt = dat[4] & 0xff;
        chrPageCnt = dat[5] & 0xff;

        mirroring = (dat[6] & 1) != 0 ? VERTICAL
                        : HORIZONTAL;
        sramEnable = (dat[6] & 2) != 0;
        trainerEnable = (dat[6] & 4) != 0;
        fourScreen = (dat[6] & 8) != 0;

        mapper = ((dat[6] & 0xff) >> 4) | (dat[7] & 0xf0);

        int romSize = 0x4000 * prgPageCnt;
        int chrSize = 0x2000 * chrPageCnt;

        romDat = new byte[romSize];
        if (chrSize != 0)
            chrDat = new byte[chrSize];
        sram = new byte[0x2000];
        vram = new byte[0x2000];

        if (romSize > 0)
            System.arraycopy(dat, 16, romDat, 0, romSize);
        if (chrSize > 0)
            System.arraycopy(dat, 16 + romSize, chrDat, 0, chrSize);

        System.out.println("Cartridge information:");
        System.out.println(romSize / 1024 + "KB rom");
        System.out.println(chrSize / 1024 + "KB vrom");
        System.out.println("mapper " + mapper);
        System.out.println((mirroring == VERTICAL ? "vertical" : "holizontal") + " mirroring");
        System.out.println("sram        : " + sramEnable);
        System.out.println("trainer     : " + trainerEnable);
        System.out.println("four screen : " + fourScreen);
    }

    public byte[] getRom() {
        return romDat;
    }

    public byte[] getChr() {
        return chrDat;
    }

    public byte[] getSram() {
        return sram;
    }

    public byte[] getVram() {
        return vram;
    }

    public int romSize() {
        return prgPageCnt;
    }

    public int chrSize() {
        return chrPageCnt;
    }

    public int mapperNo() {
        return mapper;
    }

    public boolean hasSram() {
        return sramEnable;
    }

    public boolean hasTrainer() {
        return trainerEnable;
    }

    public boolean isFourScreen() {
        return fourScreen;
    }

    public int mirror() {
        return mirroring;
    }
}