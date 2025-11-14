# Team 8

## Team introduction

Hello everyone, we are team 8 in charge of the gameplay HUD. Our Goal is to make the best Game HUD we can. 

## Team members

- Team Leader: Antoine Mao
- Developer 1: Jack O’Sullivan
- Developer 2: Man Gwan Cahill Wan
- Developer 3: Elena Denaix
- Developer 4: Yacine Ramdane
- Developer 5: Willys Martias

## Team requirements
To implement the overall gameplay HUD for the space invaders game.

Detailed requirements
- 1. Display Lives
Description: Display the player’s remaining lives on the HUD.
Functionalities:
  - Updates in real-time whenever the player loses or gains a life. 
  - Shows a visual icon or number for each remaining life.
  User Goal: The player must be able to quickly evaluate their survival status during gameplay.
  

- 2. Display what level it is
Description: Show the current level number or title on the HUD.
Functionalities:
  - Automatically updates when the player advances to a new level. 
  - Display format: “Level X” or “Stage X”. 
  - Must stay synchronized with the Level Design team’s structure and naming.
  
  
- 3. Display currency
Description: Show the total amount of in-game currency the player has collected.
Functionalities:
  - Connected to the Currency Team’s module or data source. 
  - Updates instantly after each gain or expenditure of currency. 
  - Supports large numbers. 
  - Optional visual feedback for increments.  
  

- 4. Display inventory/active Items
Description: Display the player’s owned and currently active items.
Functionalities: 
  - Receives live data from the Items Team module. 
  - Shows dynamic icons indicating active/inactive state. 
  - Displays up to 3 items simultaneously. 
  - The interface must be scalable in case of new items being added.
  

- 5. Display time
Description: Show the elapsed or remaining time during the game.
Functionalities: 
  - Must pause and resume in sync with the main game engine. 
  - Updates with one-second precision. 

  

- 6. Display player 2 information
Description: Show HUD information for the second player in multiplayer mode.
Functionalities:
  - Depends on the “2-Player Mode” module for real-time data. 
  - HUD layout should be symmetrical to Player 1’s section. 
  - Display is conditional — only active when multiplayer mode is enabled.

## Dependencies on other teams

- HUD for Items
  - Purpose: Display the player’s inventory and active items on the HUD. 
  - Dependency: Items Team to provide real-time data (item list, active/inactive status, icons, quantities...). 
  - Coordination: Requires a stable API or event system; communicate any item changes; regularly test integration.
  

- Display Currency
  - Purpose: Show the player’s in-game currency and update it instantly after gains/spending. 
  - Dependency: Currency Team to provide current values and updates. 
  - Coordination: Synchronize HUD animations with currency changes; notify of any changes in currency logic to avoid inconsistencies.
  

- Player 2 Information
  - Purpose: Display player 2’s information in multiplayer mode (lives, score, inventory, active items). 
  - Dependency: 2-Player Mode Team to provide player 2 stats and status. 
  - Coordination: Regularly test data synchronization; only display if two-player mode is active; update HUD immediately if player 2 mechanics change.
  

- Level Information 
  - Purpose: Display the current level or stage for the player.
  - Dependency: Level Design Team to provide level number, name, progress, and special events. 
  - Coordination: Provide a consistent interface; update HUD when level structure or events change; communicate early for special events requiring visual display.