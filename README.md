# Chord-Ricart-Agrawala
A distributed system implementing Ricart-Agrawala algorithm and Chord architecture for efficient decentralized mutex management and file handling.

## Introduction

This project is a distributed system that combines the Ricart-Agrawala algorithm for mutual exclusion and the Chord architecture for decentralized file handling. It ensures fair and efficient access to critical sections and provides robust file management across distributed nodes.

## Features

- **Distributed Mutex Management**: Utilizes the Ricart-Agrawala algorithm to manage critical section access across nodes.
- **Chord Architecture**: Implements a distributed hash table for node and file management.
- **File Handling**: Supports adding, removing, and querying files in a distributed environment.
- **Node Failure Handling**: Includes mechanisms to handle node failures and ensure system resilience.

## Architecture

### Ricart-Agrawala Algorithm

The Ricart-Agrawala algorithm is used to manage access to critical sections in a distributed system. Each node sends a request for access to all other nodes and waits for replies before entering the critical section.

### Chord Architecture

Chord is a distributed hash table that provides a way to efficiently locate the node that holds a particular piece of data. Each node in the system is assigned a unique identifier, and data is distributed among the nodes based on this identifier.

### File Handling

Files are added, queried, and removed across the distributed system. Each file is associated with a key, and the Chord algorithm is used to locate the node responsible for handling that file.
