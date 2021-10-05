package nes.mapper;

import nes.MapperAdapter;
import nes.Nes;

public class NullMapper extends MapperAdapter {
    public NullMapper(Nes n){
    }

    public int mapperNo() {
            return 0;
    }
}