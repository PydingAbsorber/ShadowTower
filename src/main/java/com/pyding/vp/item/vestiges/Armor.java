package com.pyding.vp.item.vestiges;

import com.pyding.vp.client.sounds.SoundRegistry;
import com.pyding.vp.util.VPUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Iterator;

public class Armor extends Vestige{
    public Armor(){
        super();
    }

    @Override
    public void dataInit(int vestigeNumber, ChatFormatting color, int specialCharges, int specialCd, int ultimateCharges, int ultimateCd, int specialMaxTime, int ultimateMaxTime, boolean hasDamage, ItemStack stack) {
        super.dataInit(11, ChatFormatting.RED, 4, 10, 1, 60, 60, 25, true, stack);
    }

    @Override
    public void doSpecial(long seconds, Player player, Level level, ItemStack stack) {
        VPUtil.play(player, SoundRegistry.FLESH.get());
        player.addEffect(new MobEffectInstance(VPUtil.getRandomEffect(false), 60 * 20));
        player.addEffect(new MobEffectInstance(VPUtil.getRandomEffect(false), 60 * 20));
        float debuffCount = 0;
        Iterator<MobEffectInstance> iterator = player.getActiveEffects().iterator();
        while (iterator.hasNext()) {
            MobEffectInstance effectInstance = iterator.next();
            MobEffect effect = effectInstance.getEffect();
            if (!effect.isBeneficial()) {
                debuffCount++;
            }
        }
        player.hurt(player.damageSources().cactus(),VPUtil.getAttack(player,true)*(0.3f + debuffCount));
        player.getPersistentData().putFloat("VPHealDebt", player.getPersistentData().getFloat("VPHealDebt")+((debuffCount+4)/4*player.getMaxHealth()*0.4f));
        if(player.getPersistentData().getFloat("VPHealDebt") > player.getMaxHealth()*6) {
            stack.getOrCreateTag().putFloat("VPArmor", stack.getOrCreateTag().getFloat("VPArmor") + 40);
            VPUtil.addShield(player,stack.getOrCreateTag().getFloat("VPArmor")*0.1f,true);
        }
        VPUtil.spawnParticles(player, ParticleTypes.CRIMSON_SPORE,3,1,0,-0.1,0,0,false);
        super.doSpecial(seconds, player, level, stack); 
    }

    @Override
    public void doUltimate(long seconds, Player player, Level level, ItemStack stack) {
        VPUtil.play(player, SoundRegistry.FLESH2.get());
        VPUtil.spawnParticles(player, ParticleTypes.CRIT,3,1,0,0,0,0,false);
        super.doUltimate(seconds, player, level, stack);
    }

    @Override
    public void ultimateEnds(Player player, ItemStack stack) {
        int pain = (int)stack.getOrCreateTag().getFloat("VPArmor");
        VPUtil.repairAll(player,pain);
        stack.getOrCreateTag().putFloat("VPArmor",0);
        super.ultimateEnds(player, stack);
    }

    @Override
    public void curioSucks(Player player, ItemStack stack) {
        stack.getOrCreateTag().putFloat("VPArmor",0);
        super.curioSucks(player, stack);
    }
}
