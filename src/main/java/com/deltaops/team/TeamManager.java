/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.team;

import com.deltaops.DeltaOpsMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamManager {
    private static final Map<UUID, Team> TEAMS = new ConcurrentHashMap<>();

    public static Team createTeam(ServerPlayer leader) {
        if (leader == null) {
            return null;
        }

        Team team = new Team(UUID.randomUUID(), leader);
        TEAMS.put(team.teamId, team);
        team.members.add(leader);
        return team;
    }

    public static Team getTeam(UUID teamId) {
        return TEAMS.get(teamId);
    }

    public static Team getTeamByPlayer(Player player) {
        if (player == null) {
            return null;
        }

        UUID playerId = player.getUUID();
        for (Team team : TEAMS.values()) {
            if (team.leader != null && team.leader.getUUID().equals(playerId)) {
                return team;
            }

            for (ServerPlayer member : team.members) {
                if (member != null && member.getUUID().equals(playerId)) {
                    return team;
                }
            }
        }
        return null;
    }

    public static void applyToTeam(ServerPlayer applicant, UUID teamId) {
        if (applicant == null || teamId == null) {
            return;
        }

        Team team = TEAMS.get(teamId);
        if (team == null) {
            return;
        }

        if (team.members.contains(applicant) || team.leader.equals(applicant)) {
            return;
        }

        if (!team.applicants.contains(applicant)) {
            team.applicants.add(applicant);
        }

        Component accept = Component.literal("[同意]")
                .withStyle(ChatFormatting.GREEN)
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dt accept " + applicant.getGameProfile().getName())));
        Component reject = Component.literal("[拒絕]")
                .withStyle(ChatFormatting.RED)
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dt reject " + applicant.getGameProfile().getName())));

        Component message = Component.literal("§e[組隊] 玩家 " + applicant.getGameProfile().getName() + " 申請加入小隊。")
                .append(accept)
                .append(Component.literal(" "))
                .append(reject);
        team.leader.sendSystemMessage(message);
    }

    public static boolean acceptApplicant(ServerPlayer leader, ServerPlayer applicant) {
        if (leader == null || applicant == null) {
            return false;
        }

        Team team = getTeamByPlayer(leader);
        if (team == null || !team.leader.equals(leader)) {
            return false;
        }

        if (!team.applicants.contains(applicant)) {
            return false;
        }

        team.applicants.remove(applicant);
        team.members.add(applicant);
        applicant.sendSystemMessage(Component.literal("§a你已加入隊伍。"));
        LobbyManager.syncToLeaderLobby(applicant, team.leader);
        return true;
    }

    public static boolean kickMember(ServerPlayer leader, ServerPlayer target) {
        if (leader == null || target == null) {
            return false;
        }

        Team team = getTeamByPlayer(leader);
        if (team == null || !team.leader.equals(leader)) {
            return false;
        }
        if (!team.members.contains(target) || target.equals(leader)) {
            return false;
        }

        team.members.remove(target);
        target.sendSystemMessage(Component.literal("§c你已被隊長移出隊伍。"));
        return true;
    }

    public static boolean transferLeader(ServerPlayer leader, ServerPlayer newLeader) {
        if (leader == null || newLeader == null) {
            return false;
        }

        Team team = getTeamByPlayer(leader);
        if (team == null || !team.leader.equals(leader)) {
            return false;
        }
        if (!team.members.contains(newLeader) || newLeader.equals(leader)) {
            return false;
        }

        team.leader = newLeader;
        newLeader.sendSystemMessage(Component.literal("§a你已成為新的隊長。"));
        return true;
    }

    public static boolean leaveTeam(ServerPlayer player) {
        if (player == null) {
            return false;
        }

        Team team = getTeamByPlayer(player);
        if (team == null) {
            return false;
        }
        if (team.leader.equals(player)) {
            team.members.remove(player);
            TEAMS.remove(team.teamId);
            return true;
        }

        team.members.remove(player);
        return true;
    }

    public static class Team {
        public final UUID teamId;
        public ServerPlayer leader;
        public final List<ServerPlayer> members = new ArrayList<>();
        public final List<ServerPlayer> applicants = new ArrayList<>();

        public Team(UUID teamId, ServerPlayer leader) {
            this.teamId = teamId;
            this.leader = leader;
        }
    }
}
