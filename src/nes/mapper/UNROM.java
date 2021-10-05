package nes.mapper;

import nes.MapperAdapter;
import nes.Nes;

public class UNROM extends MapperAdapter {
    private Nes nes;

    public UNROM(Nes n) {
        nes = n;
        reset();
    }

    public int mapperNo() {
        return 2;
    }

    public void reset() {
        int romSize = nes.getRom().romSize();
        nes.getMbc().mapRom(0, 0);
        nes.getMbc().mapRom(1, 1);
        nes.getMbc().mapRom(2, (romSize - 1) * 2);
        nes.getMbc().mapRom(3, (romSize - 1) * 2 + 1);
    }

    public void write(short adr, byte dat) {
        nes.getMbc().mapRom(0, dat * 2);
        nes.getMbc().mapRom(1, dat * 2 + 1);
    }
}