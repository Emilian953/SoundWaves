# 🎵 SoundWaves

**SoundWaves** is a music streaming application similar to Spotify, designed to simulate user interactions and track engagement from an **admin perspective**. The platform processes commands from input files to mimic actions such as **playing songs, creating playlists, subscribing to artist updates** and many more.


# 🚀 Core Features
SoundWaves is built around three main functional areas: 

1. 🎧 **The Audio Player** – Handles all playback-related features, supporting both **songs and podcasts** organized into **libraries, playlists, and podcast collections**. Users can search for content, manage playback settings, and track personal listening statistics. 

2. 🔄 **Pagination (User Roles and Content Management)** – Differentiates between **artists, hosts, and regular users**, each with unique permissions. Artists and hosts can manage content, while regular users primarily consume and explore media. 

3. 📊 **Analytics & Recommendations** – Provides **personalized insights, monetization features, and content recommendations**. Users can generate **wrapped summaries**, artists can track **streaming performance**, and hosts can analyze **listener engagement**.

## 🎼 1. The Audio Player

The **audio player** is the core feature of SoundWaves, supporting two types of content:
 - **Songs** 
 - **Episodes** 

These are organized into three types of collections: 
- **Libraries** – A global collection of all songs and podcasts available on the platform. 
- **Playlists** – User-created collections of songs, which can be public or private.
- **Podcasts** – A collection of episodes grouped by series. 

Key audio player functionalities include: 

- **Search Bar** – Allows searching for content by name, album, lyrics, genre, and other filters. 
-  **User Management** – Users can be created and assigned unique usernames. 
-  **Playback Controls** – Play, pause, repeat, shuffle, like, add to playlist, skip (next/prev). 
-  **User Stats** – Displays analytics such as: 
	-  **Top liked songs**  
	- **Top liked playlists** 
	-  **Preferred songs per user**

## 2. 🎭 Pagination (User Roles and Content Management)

SoundWaves is designed with **three distinct user roles**, each with different levels of access and functionality:
- 🎵 **Artists**:
	-  Add or remove albums. 
	-  Add or remove events. 
- 🎙 **Hosts**: 
	-  Add or remove podcasts. 
	-  Add or remove announcements. 
-  👥 **Regular Users**: 
	-  Listen to music and podcasts. 
	-  Access stats such as **top artists, top albums, and personal listening history**. 
	-  Switch between **online and offline modes**.

This structure ensures that **content creators (artists and hosts) can manage their media**, while **regular users can consume content and access analytics**.

## 📊 Analytics & Recommendations

SoundWaves integrates **advanced analytics and recommendation features**, enhancing user experience with data-driven insights: 
- 🎁 **Wrapped Feature** – Users can generate personalized summaries:
	 - **Regular Users** – See their most played songs, favorite artists, and top podcasts. 
	 -  **Artists** – View their **most streamed songs and albums**, and analyze listener engagement. 
	 -  **Hosts** – Access **listener stats** and identify their most popular episodes. 
	 
- 💰 **Monetization System**: 
	-  Artists earn revenue based on the **number of streams** their songs receive. 			
	- Users can **buy and browse merch** from their favorite artists. 
	-  **Premium vs. Free Users**: 
		-  **Premium users** enjoy an ad-free experience. 
		-  **Free users** have their playback **interrupted by ads**. 

- 🔄 **Song Recommendations**: 
		-  Users receive **personalized song suggestions** based on their listening habits. 
		- **Random song mode** and **notification system** enhance content discovery. 

This feature set makes SoundWaves a **fully functional, engaging, and monetized** music streaming simulation, providing a **comprehensive experience** for both listeners and content creators.

## 📩 Input & Output Handling

SoundWaves processes **structured JSON input commands** to simulate user interactions such as **adding users, playing music, managing playlists, and monetization features**. The system then generates a **structured JSON output** reflecting the results of these commands.

### 📥 Input Format
The input consists of a list of **JSON objects**, where each object represents a command to be executed. Key fields include: 
- **`command`** – Specifies the action (e.g., `addUser`, `playSong`, `adBreak`). 
-  **`username`** – The user executing the command (if applicable). 
-  **`timestamp`** – Indicates the time of execution relative to the start of the simulation. 
-  Additional fields depending on the command type (e.g., `price` for ads, `age` and `city` for user creation). 

#### **Example Input:** 
```json 
[
 {
  "command": "adBreak",
  "username": "frank21",
  "timestamp": 0,
  "price": 10 
  },
 { 
   "timestamp": 1, 
   "command": "addUser"
   "age":  69,
   "city": "New York",
   "type": "artist",
   "username": "The Beatles",
 },
]
```

### 📤 **Output Format**

 For each executed command, the system returns a **JSON response**, providing feedback on the command execution. The output includes: 
 - **`command`** – The executed command. 
 -  **`user`** – The affected user (if applicable). 
 -  **`timestamp`** – The execution time. 
 -  **`message`** – A status message indicating success or error. 

#### **Example Output:**
```json 
[
 {
  "command": "adBreak",
  "user": "frank21",
  "timestamp": 0,
  "message": "frank21 is not playing any music."
 },
 { 
   "command" : "addUser", 
   "user" : "The Beatles", 
   "timestamp" : 1, 
   "message" : "The username The Beatles has been added successfully."
 } 
]
```
