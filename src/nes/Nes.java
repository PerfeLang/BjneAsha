package nes;

import java.io.IOException;
import nes.mapper.*;
import ui.AWTRenderer;

public class Nes {
    private Rom rom;
    private Cpu cpu;
    private Ppu ppu;
    private Apu apu;
    private Mbc mbc;
    private Regs regs;
    private Mapper mapper;

    private AWTRenderer renderer;

    public Nes(AWTRenderer r) {
        renderer = r;
        cpu = new Cpu(this);
        apu = new Apu(this);
        ppu = new Ppu(this);
        mbc = new Mbc(this);
        regs = new Regs(this);
        rom = new Rom(this);
        mapper = null;
    }

    public void load(String fname) {
        try {
            rom.load(fname);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        mapper = makeMapper(rom.mapperNo());
        if (mapper == null)
        try
        {
            throw new IOException("unsupported mapper:" + rom.mapperNo());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        reset();
    }

public Mapper makeMapper(int num) {
    Mapper[] classes = {
        new CNROM (this),
        new MMC1 (this),
        new MMC3 (this),
        new NullMapper (this),
        new UNROM (this),
        new VRC6 (this)
    };

    for (int i = 0; i < classes.length; i++) {
        if (classes[i].mapperNo() == num)
            return classes[i];
    }

    return null;
}

    public boolean checkMapper() {
        return mapper != null;
    }

    public void reset() {
        // reset rom & mbc first
        rom.reset();
        mbc.reset();

        // reset mapper
        mapper.reset();

        // reset rest
        cpu.reset();
        apu.reset();
        ppu.reset();
        regs.reset();

        System.out.println("Reset virtual machine ...");
    }

    public void execFrame() {
        // CPU clock is 1.7897725MHz
        // 1789772.5 / 60 / 262 = 113.85...
        // 114 cycles per line?
        // 1789772.5 / 262 / 114 = 59.922 fps ?

        Renderer.ScreenInfo scri = renderer.requestScreen(256, 240);
        Renderer.SoundInfo sndi = renderer.requestSound();
        int[] buf = renderer.requestInput(2, 8);

        if (sndi != null) {
            apu.genAudio(sndi);
            renderer.outputSound(sndi);
        }
        if (buf != null)
            regs.setInput(buf);

        regs.setVBlank(false, true);
        regs.startFrame();
        for (int i = 0; i < 240; i++) {
            if (mapper != null)
                mapper.hblank(i);
            regs.startScanline();
            if (scri != null)
                ppu.render(i, scri);
            ppu.spriteCheck(i);
            apu.sync();
            cpu.exec(114);
            regs.endScanline();
        }

        if ((regs.getFrameIrq() & 0xC0) == 0)
            cpu.setIrq(true);

        for (int i = 240; i < 262; i++) {
            if (mapper != null)
                mapper.hblank(i);
            apu.sync();
            if (i == 241) {
                regs.setVBlank(true, false);
                cpu.exec(0); // one extra op will execute after VBLANK
                regs.setVBlank(regs.getIsVBlank(), true);
                cpu.exec(114);
            } else
                cpu.exec(114);
        }

        if (scri != null)
            renderer.outputScreen(scri);
    }

    public Rom getRom() {
        return rom;
    }

    public Cpu getCpu() {
        return cpu;
    }

    public Ppu getPpu() {
        return ppu;
    }

    public Apu getApu() {
        return apu;
    }

    public Mbc getMbc() {
        return mbc;
    }

    public Regs getRegs() {
        return regs;
    }

    public Mapper getMapper() {
        return mapper;
    }
}