package cn.superiormc.mythictotem.paper.adapts.v26_3;

import cn.superiormc.mythictotem.paper.adapts.ComponentCodecRegistry;
import cn.superiormc.mythictotem.paper.adapts.VersionCodecs;

public class Paper263Codecs implements VersionCodecs {

    @Override
    public void registerCodecs(ComponentCodecRegistry registry) {
        registry.register(new AttackAnimationCodec());
        registry.register(new InteractAnimationCodec());
        registry.register(new SwingAnimationCodec());
        registry.register(new WaxedCodec());
        registry.register(new CushionColorCodec());
        registry.register(new VillagerFoodCodec());
        registry.register(new ProvidesPotteryPatternCodec());
        registry.register(new MobVisibilityCodec());
        registry.register(new SignTextFrontCodec());
        registry.register(new SignTextBackCodec());
    }
}
