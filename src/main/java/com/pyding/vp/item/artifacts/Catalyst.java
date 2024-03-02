package com.pyding.vp.item.artifacts;

import com.pyding.vp.client.sounds.SoundRegistry;
import com.pyding.vp.util.VPUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Catalyst extends Vestige{
    public Catalyst(){
        super();
    }

    @Override
    public void dataInit(int vestigeNumber, ChatFormatting color, int specialCharges, int specialCd, int ultimateCharges, int ultimateCd, int specialMaxTime, int ultimateMaxTime, boolean hasDamage) {
        super.dataInit(17, ChatFormatting.GREEN, 2, 40, 1, 120, 1, 1, hasDamage);
    }
    public int debuffDefence = 0;
    @Override
    public void doSpecial(long seconds, Player player, Level level) {
        VPUtil.play(player, SoundRegistry.CATALYST1.get());
        for(LivingEntity entity: VPUtil.getEntities(player,20,false)){
            List<MobEffectInstance> list = new ArrayList<>(VPUtil.getEffectsHas(entity, false));
            VPUtil.clearEffects(entity,false);
            for(MobEffectInstance effectInstance: list){
                int duration = (int)(effectInstance.getDuration()*1.15);
                int amplifier = effectInstance.getAmplifier();
                if(Math.random() < 0.2)
                    amplifier += 1;
                entity.addEffect(new MobEffectInstance(effectInstance.getEffect(),duration,amplifier));
            }
            VPUtil.spawnParticles(player, ParticleTypes.BUBBLE,entity.getX(),entity.getY(),entity.getZ(),8,0,-0.5,0);
        }
        if(isStellar)
            debuffDefence = 5;
        Random random = new Random();
        int duration = random.nextInt(140)+60;
        int power = random.nextInt(5);
        player.addEffect(new MobEffectInstance(VPUtil.getRandomEffect(true),duration*20,power));
        super.doSpecial(seconds, player, level);
    }

    @Override
    public void doUltimate(long seconds, Player player, Level level) {
        VPUtil.play(player, SoundRegistry.CATALYST2.get());
        Random random = new Random();
        int stolen = 0;
        for(LivingEntity entity: VPUtil.getEntities(player,25,false)){
            List<MobEffectInstance> list = new ArrayList<>();
            for(MobEffectInstance instance: VPUtil.getEffectsHas(entity, true)){
                if(instance.getAmplifier() <= 4 || isStellar) {
                    list.add(instance);
                    entity.removeEffect(instance.getEffect());
                    stolen++;
                }
            }
            for(MobEffectInstance effectInstance: list){
                player.addEffect(new MobEffectInstance(effectInstance.getEffect(),effectInstance.getDuration(),effectInstance.getAmplifier()));
                entity.addEffect(new MobEffectInstance(VPUtil.getRandomEffect(false),10*20,random.nextInt(3)));
            }
            VPUtil.spawnParticles(player, ParticleTypes.BUBBLE_COLUMN_UP,entity.getX(),entity.getY(),entity.getZ(),8,0,-0.5,0);
        }
        if(stolen > 0){
            List<MobEffectInstance> list = new ArrayList<>();
            for(MobEffectInstance instance: VPUtil.getEffectsHas(player, true)){
                if(instance.getAmplifier() <= 4 || isStellar) {
                    list.add(instance);
                    player.removeEffect(instance.getEffect());
                }
            }
            for(MobEffectInstance effectInstance: list){
                player.addEffect(new MobEffectInstance(effectInstance.getEffect(),effectInstance.getDuration()*(1+stolen/10),effectInstance.getAmplifier()));
            }
        }
        super.doUltimate(seconds, player, level);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        if(debuffDefence > 0) {
            debuffDefence--;
            for (MobEffectInstance instance : VPUtil.getEffectsHas(player, false)){
                for(LivingEntity livingEntity: VPUtil.getEntitiesAround(player,15,15,15,false)){
                    livingEntity.addEffect(new MobEffectInstance(instance.getEffect(),instance.getDuration(),instance.getAmplifier()));
                }
                player.removeEffect(instance.getEffect());
                break;
            }
        }
        super.curioTick(slotContext, stack);
    }
}
