# Virtual Memory Simulator

<img width="1909" height="966" alt="image (1)" src="https://github.com/user-attachments/assets/9d25a0af-309f-491b-9aec-3a79a935dedb" />



A comprehensive, full-stack simulation of an Operating System's memory management architecture. This application visualizes the complex interaction between Hardware (MMU/TLB) and Software (OS/Page Tables) during memory access operations.


##  Key Features

*   **Interactive Memory Access:** Perform **Read** and **Write** operations on specific Virtual Addresses and observe the translation process in real-time.

*   **Hardware Emulation:**
    *   **TLB (Translation Lookaside Buffer):** Simulates Set-Associative caching, hits, misses, and evictions.
    *   **MMU (Memory Management Unit):** Handles address translation and hardware flag updates (Dirty/Reference bits).

*   **Intel-Based Page Tables:** Implements a realistic **Two-Level Page Table Directory** structure (Directory $\to$ Table $\to$ Entry) for address mapping.

*   **Memory Visualization:**
    *   **Physical Memory (RAM):** View the state of allocated Frames and their data bytes.
    *   **Virtual Memory (Disk/Backing Store):** Visualize pages swapped out to disk.

*   **Algorithmic Engine:** Supports multiple Page Replacement Algorithms including **FIFO**, **LRU**, and **Clock (Second-Chance)**.

---

##  Supported Scenarios & Use Cases

This simulator handles the full lifecycle of memory access for both **Read** and **Write** operations, covering three distinct workflows:

### 1. The Fast Path (Hardware Only) ⚡
*Scenarios where the page is already accessible in RAM. The simulator demonstrates the speed of hardware caching.*

*   **TLB Hit:** The translation is found instantly in the TLB cache.
*   **TLB Miss (Warm-Up):** The page is in RAM, but not in the TLB. A **Hardware Page Table Walk** retrieves the mapping.
*   **TLB Eviction:** LRU logic evicts old translations when the TLB is full.
*   **Dirty Bit Update (Write Only):** On a successful Write, the hardware automatically sets the Dirty Bit (D=1).

### 2. The Slow Path (Hardware + OS Cooperation) 🐢
*Scenarios requiring Operating System intervention (Page Faults).*

*   **Simple Page Fault:** The page is on Disk. The OS finds a free frame and loads data.
*   **"Clean Swap" (Eviction):** RAM is full. The OS evicts an unmodified page to make room.
*   **"Dirty Swap" (Write-Back):** RAM is full and the victim page is "Dirty" (D=1). The simulator performs a **Write-Back** to disk to save changes before loading the new page.

### 3. The Failure Path (OS as Protector) 🛑
*Scenarios where memory protection prevents corruption.*

*   **Segmentation Fault:** Attempting to access an address that was never allocated.
*   **Protection Fault:** Attempting to **Write** to a Read-Only page.

---

##  Technical Architecture

### Address Translation (Intel 32-bit Model)
The system parses virtual addresses using bitwise manipulation to extract:
1.  **Page Directory Index (PDI):** Level 1 lookup.
2.  **Page Table Index (PTI):** Level 2 lookup.
3.  **Offset:** Location within the specific frame.

### Tech Stack
*   **Backend:** Java (Spring Boot) - Handles MMU logic, OS algorithms, and State Management.
*   **Frontend:** React.js - Renders visualization grids for RAM, TLB, and Disk.
*   **API:** RESTful endpoints for memory access and configuration.

---

##  How to Run

### Prerequisites
*   Java 17+
*   Node.js & npm

### Steps
1.  **Start Backend:**
    Navigate to `backend/` and run the Spring Boot application.
    ```bash
    cd backend
    ./mvnw spring-boot:run
    ```

2.  **Start Frontend:**
    Navigate to `frontend/` and start the React server.
    ```bash
    cd frontend
    npm install
    npm start
    ```

3.  **Access:** Open `http://localhost:3000` in your browser.
