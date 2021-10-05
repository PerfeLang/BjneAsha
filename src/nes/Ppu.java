package nes;

public class Ppu {
    public final static int HOLIZONTAL = 0;
    public final static int VERTICAL = 1;
    public final static int FOUR_SCREEN = 2;
    public final static int SINGLE_SCREEN = 3;

    private byte[] sprram = new byte[0x100];
    private byte[][] nameTable = new byte[4][0x400];
    private byte[][] namePage = new byte[4][];
    private byte[] palette = new byte[0x20];

    private int[] nesPalette24 = new int[0x40];

    private byte[] buf = new byte[256 + 16];

    private Nes nes;

    public Ppu(Nes n) {
        nes = n;
        initPalette();
    }

    public void reset() {
        for (int i = 0; i < sprram.length; i++)
            sprram[i] = (byte) 0x00;

        for (int i = 0; i < nameTable[0].length; i++)
            nameTable[0][i] = (byte) 0x00;

        for (int i = 0; i < nameTable[1].length; i++)
            nameTable[1][i] = (byte) 0x00;

        for (int i = 0; i < nameTable[2].length; i++)
            nameTable[2][i] = (byte) 0x00;

        for (int i = 0; i < nameTable[3].length; i++)
            nameTable[3][i] = (byte) 0x00;

        for (int i = 0; i < palette.length; i++)
            palette[i] = (byte) 0x00;

        if (nes.getRom().isFourScreen())
            setMirroring(FOUR_SCREEN);
        else if (nes.getRom().mirror() == Rom.HORIZONTAL)
            setMirroring(HOLIZONTAL);
        else if (nes.getRom().mirror() == Rom.VERTICAL)
            setMirroring(VERTICAL);
        else
            setMirroring(FOUR_SCREEN);
    }

    public void setMirroring(int mt) {
        switch (mt) {
        case HOLIZONTAL:
            setMirroring(0, 0, 1, 1);
            break;
        case VERTICAL:
            setMirroring(0, 1, 0, 1);
            break;
        case FOUR_SCREEN:
            setMirroring(0, 1, 2, 3);
            break;
        case SINGLE_SCREEN:
            setMirroring(0, 0, 0, 0);
            break;
        }
    }

    public void setMirroring(int m0, int m1, int m2, int m3) {
        namePage[0] = nameTable[m0];
        namePage[1] = nameTable[m1];
        namePage[2] = nameTable[m2];
        namePage[3] = nameTable[m3];
    }

    public void render(int line, Renderer.ScreenInfo scri) {
        for(int i = 0; i < buf.length; i++)
            buf[i] = palette[0];

        if (nes.getRegs().getBgVisible())
            renderBG(line, buf);
        if (nes.getRegs().getSpriteVisible())
            renderSPR(line, buf);

        int dest = scri.width * line;
        for (int i = 0; i < 256; i++) {
            int c = nesPalette24[buf[i + 8] & 0x3f];
            int rgb = (byte) (c & 0xff);
            rgb = (rgb << 8) + (byte) ((c >> 8) & 0xff);
            rgb = (rgb << 8) + (byte) ((c >> 16) & 0xff);
            scri.buf[dest] = rgb;
            dest++;
        }
    }

    public byte[] getSpriteRam() {
        return sprram;
    }

    public byte[][] getNameTable() {
        return nameTable;
    }

    public byte[][] getNamePage() {
        return namePage;
    }

    public byte[] getPalette() {
        return palette;
    }

    private void initPalette() {
        int[][] palDat = new int[][] { { 0x75, 0x75, 0x75 },
                    { 0x27, 0x1B, 0x8F }, { 0x00, 0x00, 0xAB },
                    { 0x47, 0x00, 0x9F }, { 0x8F, 0x00, 0x77 },
                    { 0xAB, 0x00, 0x13 }, { 0xA7, 0x00, 0x00 },
                    { 0x7F, 0x0B, 0x00 }, { 0x43, 0x2F, 0x00 },
                    { 0x00, 0x47, 0x00 }, { 0x00, 0x51, 0x00 },
                    { 0x00, 0x3F, 0x17 }, { 0x1B, 0x3F, 0x5F },
                    { 0x00, 0x00, 0x00 }, { 0x05, 0x05, 0x05 },
                    { 0x05, 0x05, 0x05 },

                    { 0xBC, 0xBC, 0xBC }, { 0x00, 0x73, 0xEF },
                    { 0x23, 0x3B, 0xEF }, { 0x83, 0x00, 0xF3 },
                    { 0xBF, 0x00, 0xBF }, { 0xE7, 0x00, 0x5B },
                    { 0xDB, 0x2B, 0x00 }, { 0xCB, 0x4F, 0x0F },
                    { 0x8B, 0x73, 0x00 }, { 0x00, 0x97, 0x00 },
                    { 0x00, 0xAB, 0x00 }, { 0x00, 0x93, 0x3B },
                    { 0x00, 0x83, 0x8B }, { 0x11, 0x11, 0x11 },
                    { 0x09, 0x09, 0x09 }, { 0x09, 0x09, 0x09 },

                    { 0xFF, 0xFF, 0xFF }, { 0x3F, 0xBF, 0xFF },
                    { 0x5F, 0x97, 0xFF }, { 0xA7, 0x8B, 0xFD },
                    { 0xF7, 0x7B, 0xFF }, { 0xFF, 0x77, 0xB7 },
                    { 0xFF, 0x77, 0x63 }, { 0xFF, 0x9B, 0x3B },
                    { 0xF3, 0xBF, 0x3F }, { 0x83, 0xD3, 0x13 },
                    { 0x4F, 0xDF, 0x4B }, { 0x58, 0xF8, 0x98 },
                    { 0x00, 0xEB, 0xDB }, { 0x66, 0x66, 0x66 },
                    { 0x0D, 0x0D, 0x0D }, { 0x0D, 0x0D, 0x0D },

                    { 0xFF, 0xFF, 0xFF }, { 0xAB, 0xE7, 0xFF },
                    { 0xC7, 0xD7, 0xFF }, { 0xD7, 0xCB, 0xFF },
                    { 0xFF, 0xC7, 0xFF }, { 0xFF, 0xC7, 0xDB },
                    { 0xFF, 0xBF, 0xB3 }, { 0xFF, 0xDB, 0xAB },
                    { 0xFF, 0xE7, 0xA3 }, { 0xE3, 0xFF, 0xA3 },
                    { 0xAB, 0xF3, 0xBF }, { 0xB3, 0xFF, 0xCF },
                    { 0x9F, 0xFF, 0xF3 }, { 0xDD, 0xDD, 0xDD },
                    { 0x11, 0x11, 0x11 }, { 0x11, 0x11, 0x11 } };

        for (int i = 0; i < 0x40; i++)
            nesPalette24[i] = palDat[i][0] | (palDat[i][1] << 8)
                        | (palDat[i][2] << 16);
    }

    private byte readNameTable(short adr) {
        return namePage[((adr) >> 10) & 3][(adr) & 0x3ff];
    }

    private byte readPatTable(short adr) {
        return nes.getMbc().readChrRom(adr);
    }

    private void renderBG(int line, byte[] buf) {
        Regs r = nes.getRegs();
        int x_ofs = r.getPpuAdrX() & 7;
        int y_ofs = (r.getPpuAdrV() >> 12) & 7;
        short name_adr = (short) (r.getPpuAdrV() & 0xfff);
        short pat_adr = (short) (r.getBgPatAdr() ? 0x1000 : 0x0000);

        int ix = -x_ofs;
        for (int i = 0; i < 33; i++, ix += 8) {
            byte tile = readNameTable((short) (0x2000 + name_adr));

            byte l = readPatTable((short) (pat_adr + (tile & 0xff) * 16 + y_ofs));
            byte u = readPatTable((short) (pat_adr + (tile & 0xff) * 16 + y_ofs + 8));

            int tx = name_adr & 0x1f, ty = (name_adr >> 5) & 0x1f;
            short attr_adr = (short) ((name_adr & 0xC00) + 0x3C0
                            + ((ty & ~3) << 1) + (tx >> 2));
            int aofs = ((ty & 2) == 0 ? 0 : 4) + ((tx & 2) == 0 ? 0 : 2);
            int attr = ((readNameTable((short) (0x2000 + attr_adr)) >> aofs) & 3) << 2;

            for (int j = 7; j >= 0; j--) {
                int t = ((l & 1) | (u << 1)) & 3;
                if (t != 0)
                        buf[8 + ix + j] = (byte) (0x40 | palette[t | attr]);
                l >>= 1;
                u >>= 1;
            }

            if ((name_adr & 0x1f) == 0x1f)
                name_adr = (short) ((name_adr & ~0x1f) ^ 0x400);
            else
                name_adr++;
        }
    }

    private void renderSPR(int line, byte[] buf) {
        int spr_height = nes.getRegs().getSpriteSize() ? 16 : 8;
        int pat_adr = nes.getRegs().getSpritePatAdr() ? 0x1000 : 0x0000;

        for (int i = 0; i < 64; i++) {
            int spr_y = (sprram[i * 4 + 0] & 0xff) + 1;
            int attr = sprram[i * 4 + 2] & 0xff;

            if (!(line >= spr_y && line < spr_y + spr_height))
                continue;

            boolean is_bg = ((attr >> 5) & 1) != 0;
            int y_ofs = line - spr_y;
            int tile_index = sprram[i * 4 + 1] & 0xff;
            int spr_x = sprram[i * 4 + 3] & 0xff;
            int upper = (attr & 3) << 2;

            boolean h_flip = (attr & 0x40) == 0;
            int sx = h_flip ? 7 : 0, ex = h_flip ? -1 : 8, ix = h_flip ? -1 : 1;

            if ((attr & 0x80) != 0)
                y_ofs = spr_height - 1 - y_ofs;

            short tile_adr;
            if (spr_height == 16)
                tile_adr = (short) ((tile_index & ~1) * 16
                                + ((tile_index & 1) * 0x1000) + (y_ofs >= 8 ? 16 : 0) + (y_ofs & 7));
            else
                tile_adr = (short) (pat_adr + tile_index * 16 + y_ofs);

            byte l = readPatTable(tile_adr);
            byte u = readPatTable((short) (tile_adr + 8));

            for (int x = sx; x != ex; x += ix) {
                int lower = (l & 1) | ((u & 1) << 1);
                if (lower != 0 && (buf[8 + spr_x + x] & 0x80) == 0) {
                    if (!is_bg || (buf[8 + spr_x + x] & 0x40) == 0)
                        buf[8 + spr_x + x] = palette[0x10 | upper | lower];
                    buf[8 + spr_x + x] |= 0x80;
                }
                l >>= 1;
                u >>= 1;
            }
        }
    }

    public void spriteCheck(int line) {
        if (nes.getRegs().getSpriteVisible()) {
            int spr_y = (sprram[0] & 0xff) + 1;
            int spr_height = nes.getRegs().getSpriteSize() ? 16 : 8;
            int pat_adr = nes.getRegs().getSpritePatAdr() ? 0x1000 : 0x0000;

            if (line >= spr_y && line < spr_y + spr_height) {
                int y_ofs = line - spr_y;
                int tile_index = sprram[1] & 0xff;
                if ((sprram[2] & 0x80) != 0)
                    y_ofs = spr_height - 1 - y_ofs;
                short tile_adr;
                if (spr_height == 16)
                    tile_adr = (short) ((tile_index & ~1) * 16
                                + ((tile_index & 1) * 0x1000)
                                + (y_ofs >= 8 ? 16 : 0) + (y_ofs & 7));
                else
                    tile_adr = (short) (pat_adr + tile_index * 16 + y_ofs);
                byte l = readPatTable(tile_adr);
                byte u = readPatTable((short) (tile_adr + 8));
                if (l != 0 || u != 0)
                    nes.getRegs().setSprite0Occur(true);
            }
        }
    }
}