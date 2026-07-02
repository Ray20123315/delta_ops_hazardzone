# Delta Ops: Hazard Zone

A hardcore extraction shooter mod for Minecraft 1.20.1 Forge.

The goal of this project is to bring the hardcore survival loop of "looting, scavenging, extraction, downed rescue, and death drops" into Minecraft. It utilizes an expandable, data-driven architecture to integrate weapons, medical supplies, backpacks, operators, and future external mod integrations.

## What the Current Version Can Do

Currently, this mod can fully run a basic extraction gameplay loop. You can enter a map with gear or do a "zero-to-hero" run (hatchet run). You can scavenge for loot, fight AI or other players, and head to an extraction point to extract. If you take fatal damage, you won't die instantly; instead, you will enter a downed state, waiting for a teammate to hold right-click to rescue you, or you can self-revive by securing a kill.

In short, the following core features are already implemented:

- Extraction points and extraction countdowns
- Tactical backpacks and secure containers
- Medical system (Blacked-out parts / Bleeding / Painkillers / Surgery kits)
- Downed state, rescue mechanics, and repeated down penalties
- Corpse visuals and Corpse Bags (Death bags)
- AI / Player death drops and scavenging mechanics
- Loot crates and data-driven loot tables
- Basic KubeJS crafting recipes

## Core Gameplay Loop

### 1. Infiltration (Entering the Map)

Players can bring custom gear into the map or enter with nothing. The main focus after spawning is not just grinding mobs, but rather:

- Scavenging high-value loot scattered across the map
- Eliminating AI soldiers or other players
- Locating an extraction point and successfully extracting

### 2. Scavenging & Combat

Currently implemented loot sources include:

- Loot crates
- Hostile mob death drops
- Corpse bags from dead players
- Items safely retained in the Secure Container after extraction

### 3. Extraction

Once a player stands on an extraction point, a countdown begins. After the countdown completes, the player is teleported back to the overworld spawn point, and a successful extraction message is broadcast. 

If the player leaves the extraction zone midway, the countdown is canceled.

## Implemented Systems

## 1. Equipment System

### Item Categories

Items are currently categorized into several main types:

- Medical Items
- Ammunition
- Equipment / Gear
- High-Value Loot
- Weapons

### Tactical Backpacks & Secure Containers

This system replaces the vanilla inventory with a 2D grid-based item management system:

- Items have specific width and height dimensions
- Items can be rotated
- Items can be placed into or taken out of the grid
- Independent packet synchronization
- **Secure Containers:** Items inside are retained upon death
- **Normal Grid Backpacks:** Items inside are lost upon death

### Currently Available Items

Registered items currently include:

- Tourniquet
- Bandage
- Surgery Kit
- Medkit
- Painkillers
- Rifle Magazine
- Pistol Magazine
- Ammo Box
- Tactical Vest
- Tactical Backpack
- Night Vision Goggles
- Gold Bar
- Intel Data
- Ancient Relic
- Medical Supplies
- Assault Rifle
- Submachine Gun (SMG)
- Shotgun
- Sniper Rifle
- Pistol

## 2. Medical & Health System

This is currently one of the most complete core systems.

### Body Part Health

Health is divided into seven specific body parts:

- Head
- Chest
- Abdomen
- Left Arm
- Right Arm
- Left Leg
- Right Leg

### Blacked-Out Effects (Zero HP)

When a body part's health reaches zero (blacked-out), specific debuffs are applied:

- **Blacked Leg:** Movement speed is reduced
- **Blacked Arm:** Mining/Use speed is reduced
- **Blacked Abdomen:** Hunger depletes much faster
- **Bleeding:** Continuous health loss over time

### Usable Medical Items

- **Tourniquet:** Stops bleeding
- **Bandage:** Basic health restoration
- **Surgery Kit:** Restores blacked-out body parts to a functional state
- **Painkillers:** Temporarily suppresses the debuffs of blacked-out parts
- **Medkit:** Advanced health restoration

### Current Mechanics

- Using painkillers temporarily suppresses blacked-out side effects
- Surgery kits restore blacked-out parts to a usable state
- Bleeding causes periodic damage
- Fall damage / Gunshot wounds can progress toward a downed state or death

## 3. Downed & Rescue System

This is a major highlight of the current build.

### Downed Rules

When a player takes fatal damage, they do not die immediately but enter a downed state:

- 60-second base downed countdown
- Cannot attack or use items while downed
- Movement, vision, and actions are severely restricted

### Rescue Rules

- Teammates must HOLD right-click to continuously rescue the downed player
- Releasing the button will immediately interrupt the process
- Retrying the rescue restarts the progress bar
- A successful rescue revives the player and restores a portion of their health

### Repeated Down Penalties

A penalty mechanism for going down repeatedly is implemented:

- Each subsequent down compresses (shortens) the downed timer
- The pressure to rescue increases with repeated downs
- Prevents players from rushing mindlessly, emphasizing tactical retreats and teamwork

### Self-Revive Rules

If a player manages to secure a kill while in the downed state, it triggers a self-revive, restoring their mobility.

## 4. Corpses & Corpse Bags

Death is no longer just a simple item drop.

### Corpse Visuals

Upon death, a visual corpse marker is generated at the location of death as a visual cue.

### Corpse Bag

Dropped loot is gathered into a lootable "Corpse Bag" block:

- Player death drops are centralized into this bag
- The contents of the player's grid backpack are transferred into it
- Secure Container contents remain with the player based on retention logic
- Other players can right-click to loot the Corpse Bag

This makes death not just a penalty, but also creates a risk-reward hotspot for scavengers.

## 5. Scavenging Mechanics

### Loot Crates

There are currently three tiers of loot crates:

- Normal
- Rare
- Epic

Loot crate contents are driven by JSON loot tables, meaning they are not hardcoded.

### Loot Sources

Current sources of loot include:

- Loot Crates
- Hostile Mob Drops
- Corpse Bags
- Static loot spawn points on the map

### Advantages of Data-Driven Loot

This allows for easy adjustments without needing to recompile Java code:

- Modifying loot tables changes the drops instantly
- Easy to set up event-specific drops
- Differentiated loot pools for normal mobs, elites, and bosses

## 6. AI & Combat Drops

Basic logic for hostile mob death drops is already in place:

- Hostile mobs drop gear and tactical supplies upon death
- Prioritizes dropping their main hand, off-hand, and armor items
- Randomly drops medical items, magazines, backpacks, and high-value loot

This serves as the foundational interface for future integration with TaCZ (Timeless and Classics Zero) or Tactical AI mods.

## 7. Extraction System

### Extraction Points

- Standing on the extraction point initiates the countdown
- Leaving the extraction zone cancels the process
- Completing the countdown results in a successful extraction

### Extraction Success Effects

- The player is teleported back to the overworld spawn point
- A success message is broadcasted in the chat
- Items in the Secure Container are retained

## 8. Operator Data System

The underlying data architecture for Operators is established, including:

- Operator selection data
- Operator ability data structures
- Ability preservation during player respawns/cloning

This part is currently focused on the data and expansion layer. It has not yet been fully integrated into an in-game GUI or a complete skill tree, but the structural foundation is ready.

## Controls

### Currently Available Actions

- Open Tactical Backpack
- Open Secure Container
- Place, remove, and rotate items within the grid
- Right-click to use medical items
- Wait for a teammate to hold right-click to rescue when downed
- Wait for the countdown bar at an extraction point

### Backpack Controls

- **Left-click:** Pick up / Extract item
- **Right-click / Inventory Controls:** Place items and sync grid
- **`R` Key:** Rotate items (for items that support rotation)

## Data & Content Files

Much of this project's content is data-driven:

- `src/main/resources/data/...` : Loot tables
- `src/main/resources/assets/...` : Languages, models, blockstates
- `kubejs/server_scripts/...` : Basic crafting recipes

This means future expansions can largely be handled via JSON / KubeJS without necessarily writing new Java code.

## Current Project Status

Currently, this mod is no longer just a skeleton; it features the following playable elements:

- Playable extraction loop
- Post-death downed & rescue mechanics
- Basic scavenging and loot retention
- Medical items to treat blacked-out parts and bleeding
- Lootable corpse bags
- Customizable drops via loot tables

## Recommended Next Steps for Integration

To fully realize the "Hazard Zone" experience, the most suitable next steps are:

- Refining specific static loot spawn types within maps
- Setting up specific drop tables for AI soldiers and bosses
- Implementing detailed weight and protection differences for armor/helmets/backpacks
- Fleshing out Operator skills and team roles
- Deeper integration with external firearm mods

## Note

This README only describes the features currently implemented in the code. Large-scale external mod integrations will be added gradually based on actual future needs.
