package com.deltaops.operator;

import com.deltaops.DeltaOpsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Operator {
    public static final DeferredRegister<Operator> OPERATORS = DeferredRegister.create(
        new ResourceLocation(DeltaOpsMod.MOD_ID, "operator"), 
        DeltaOpsMod.MOD_ID
    );

    public static final RegistryObject<Operator> DEEP_BLUE = register("deep_blue",
        new Operator("deep_blue", "深藍", "Deep Blue",
            "防爆套裝", "多功能鉤爪槍", "刀片刺網手雷", "前方防護", "鋼鐵守勢"));

    public static final RegistryObject<Operator> RED_WOLF = register("red_wolf",
        new Operator("red_wolf", "紅狼", "Red Wolf",
            "動力外骨骼", "三聯裝手炮", "突破型煙霧彈", "戰術滑鏟", "重裝壓制"));

    public static final RegistryObject<Operator> WEI_LONG = register("wei_long",
        new Operator("wei_long", "威龍", "Wei Long",
            "虎蹲炮", "動力推進", "磁吸炸彈", "動能輔助系統", "山崩衝擊"));

    public static final RegistryObject<Operator> WU_MING = register("wu_ming",
        new Operator("wu_ming", "無名", "Wu Ming",
            "靜默潛襲", "旋刀飛行器", "突破型閃光彈", "重傷延滯", "無聲處決"));

    public static final RegistryObject<Operator> JI_FENG = register("ji_feng",
        new Operator("ji_feng", "疾風", "Ji Feng",
            "緊急迴避裝置", "戰術翻滾", "鑽牆電刺", "爆發型輔助脊椎", "高速突進"));

    public static final RegistryObject<Operator> FENG_YI = register("feng_yi",
        new Operator("feng_yi", "蜂醫", "Feng Yi",
            "激素槍", "煙幕", "蜂巢科技煙霧彈", "專業救援", "急救共鳴"));

    public static final RegistryObject<Operator> GU = register("gu",
        new Operator("gu", "蠱", "Gu",
            "\"流熒\"集群系統", "腎上腺素激活", "致盲毒霧", "高效治療", "毒域掌控"));

    public static final RegistryObject<Operator> DIE = register("die",
        new Operator("die", "蝶", "Die",
            "\"蝶式\"救援無人機", "納米醫療粉塵", "遙控煙霧", "體徵監測", "生命鏈接"));

    public static final RegistryObject<Operator> SHEPHERD = register("shepherd",
        new Operator("shepherd", "牧羊人", "Shepherd",
            "聲波震懾", "聲波陷阱", "強化型破片手雷", "減振防禦", "回音護盾"));

    public static final RegistryObject<Operator> URURU = register("ururu",
        new Operator("ururu", "烏魯魯", "Ururu",
            "巡飛彈", "速凝掩體", "複合型燃燒彈", "久經沙場", "火線壓制"));

    public static final RegistryObject<Operator> ECHO = register("echo",
        new Operator("echo", "回響", "Echo",
            "回聲探測器", "次聲波干擾器", "複合型閃光彈", "聲音感知", "回波定位"));

    public static final RegistryObject<Operator> BIT = register("bit",
        new Operator("bit", "比特", "Bit",
            "巡獵蜘蛛", "哨兵母巢", "智能煙霧地雷", "防守尖兵", "網狀防區"));

    public static final RegistryObject<Operator> LIQUID_NITROGEN = register("liquid_nitrogen",
        new Operator("liquid_nitrogen", "液氮", "Liquid Nitrogen",
            "連發冷凝榴彈", "杜瓦冷罐", "溫感追蹤震撼彈", "霜化反應", "凍結邊界"));

    public static final RegistryObject<Operator> LUNA = register("luna",
        new Operator("luna", "露娜", "Luna",
            "偵察箭矢", "電擊箭矢", "增強型破片手雷", "敵情分析", "高空標記"));

    public static final RegistryObject<Operator> HACK_CLAW = register("hack_claw",
        new Operator("hack_claw", "駭爪", "Hack Claw",
            "信號破譯器", "閃光巡飛器", "數據飛刀", "隱匿消聲", "黑入脈衝"));

    public static final RegistryObject<Operator> SILVER_WING = register("silver_wing",
        new Operator("silver_wing", "銀翼", "Silver Wing",
            "蜂鳥間諜攝像頭", "獵鷹無人機", "脈衝手雷", "蹤跡探查", "高空偵壓"));

    private static RegistryObject<Operator> register(String name, Operator operator) {
        return OPERATORS.register(name, () -> operator);
    }

    private final String id;
    private final String nameZhTw;
    private final String nameEnUs;
    private final String traitZhTw;
    private final String skill1ZhTw;
    private final String skill2ZhTw;
    private final String skill3ZhTw;
    private final String skill4ZhTw;

    public Operator(String id, String nameZhTw, String nameEnUs,
                    String traitZhTw, String skill1ZhTw, String skill2ZhTw, String skill3ZhTw, String skill4ZhTw) {
        this.id = id;
        this.nameZhTw = nameZhTw;
        this.nameEnUs = nameEnUs;
        this.traitZhTw = traitZhTw;
        this.skill1ZhTw = skill1ZhTw;
        this.skill2ZhTw = skill2ZhTw;
        this.skill3ZhTw = skill3ZhTw;
        this.skill4ZhTw = skill4ZhTw;
    }

    public String getId() {
        return id;
    }

    public String getNameZhTw() {
        return nameZhTw;
    }

    public String getNameEnUs() {
        return nameEnUs;
    }

    public String getTraitZhTw() {
        return traitZhTw;
    }

    public String getSkill1ZhTw() {
        return skill1ZhTw;
    }

    public String getSkill2ZhTw() {
        return skill2ZhTw;
    }

    public String getSkill3ZhTw() {
        return skill3ZhTw;
    }

    public String getSkill4ZhTw() {
        return skill4ZhTw;
    }

    public ResourceLocation getRegistryName() {
        return new ResourceLocation(DeltaOpsMod.MOD_ID, id);
    }
}