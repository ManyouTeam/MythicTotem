package cn.superiormc.mythictotem.paper.adapts.v1_21_6;

import cn.superiormc.mythictotem.paper.adapts.ComponentCodecRegistry;
import cn.superiormc.mythictotem.paper.adapts.VersionCodecs;

public class Paper1216Codecs implements VersionCodecs {

    @Override
    public void registerCodecs(ComponentCodecRegistry registry) {
        registry.register(new AttributesCodec());
        registry.register(new EquippableCodec());
    }

}
