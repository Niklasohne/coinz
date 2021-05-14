# Coinz (v1.0)

This is a simple Economy & Shop Plugin for Minecraft Paper Server.

The Plugin explicitly don't use Vault etc.

The goal was to make a Working economy Plugin within 1-2 days of workTime (currently ~15h) 
after an 2-year break of actively programming Java plugins to get back into it.

But I still want to work on this in the future

## Content
there are 2 main parts to this Plugin : Economy management and Shop

### Economy
<img src= "https://cdn.discordapp.com/attachments/842801422642708561/842801597280026654/2021-05-14_14.14.16.png" width="400">

The player has 2 locations to hold his money

#### Wallet 
Cash, the player always is taking around. With this the player can buy stuff in Shops

#### Bank Account 
The Money stored in the Bank is safe. 
The Player can not use it to buy stuff in Shops, but can store it safely

(in the future you can even get Interest)

when you open the Bank-Menu, you have the option to pay some money in from your wallet, or get some for your wallet

Also, you can see your last 10 transactions and send Money directly to Other players


<img src= "https://cdn.discordapp.com/attachments/842801422642708561/842801655816519700/2021-05-14_14.13.10.png" width="400">
<img src= "https://cdn.discordapp.com/attachments/842801422642708561/842801627295252501/2021-05-14_14.13.23.png" width="400">

### Shop
Currently the Shops are only hardcoded and there for demonstration

you have a set of Items each individual Merchants sells

<img src= "https://cdn.discordapp.com/attachments/842801422642708561/842801681908498512/2021-05-14_14.12.54.png" width="400">



## Setup
to use this plugin you need an MongoDB database to store Player-information etc.

take the compiled jar [link will follow]() and drag it into the Plugin folder

insert your Database connection string into the config file

-> you are ready to go

### create NPC

go to the location in the world where you want the NPC and use the `/spawn_npc`

at the moment there are only 3 available, hardcoded NPC's

- create an Bank NPC -> `/spawn_npc BANK`
- create an BlockMerchant -> `/spawn_npc BLOCKMERCHANT`
- create an BlackSmith -> `/spawn_npc BLACKSMITH`



## Future work

BugFixes
- currently, there is no way to remove an NPC

Economy
- player looses wallet on death
- Interest in money on the Bank

Merchants
- Items not hardcoded but in Database
- Merchants not hardcoded but in Database
- A simple interface to create and manage Items and Merchants
- ability to sell items to merchants