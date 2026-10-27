# CS 205 Programming Assignments

Java + [Processing](https://processing.org/) projects from CS 205 at Mount Holyoke College (Fall 2026).

## PA1: Tone Matrix

A 16 × 16 grid music sequencer. Click cells to light them up; a sweep moves across the columns and plays the notes of every lit cell in the active column.

- **Part 1** (`StringInstrument`): synthesizes a plucked-string sound with the **Karplus-Strong algorithm**: a buffer one wavelength long is "plucked" into a square wave, then each sample is replaced by the average of itself and its neighbor times a decay factor, so the wave smooths into a pitched, fading tone.
- **Part 2** (`ToneMatrix`): the interactive grid, using two parallel 2D arrays: `lightGrid` for which cells are on and `audioSampleGrid` for the note each cell plays.

## PA2: Plaisante

A poker-hand card game. Eight cards are dealt; the player selects up to five and plays them for a score of total card value × hand multiplier, with 4 hands and 4 discards per round.

- **Part 1** (`Card`): draws a playing card and animates it toward a target position.
- **Part 2** (`Hand`, `Tableau`): game logic. `Hand` identifies the best poker hand among the selected cards (pair, two pair, three of a kind, straight, flush, full house, four of a kind) and scores it; `Tableau` tracks cards as they move between the held cards, the selection and the play area.

## What I wrote vs. what was provided

| My code | Provided by course staff |
| --- | --- |
| `StringInstrument.java`, `ToneMatrix.java`, `Card.java`, `Hand.java`, `Tableau.java` | `StringInstrumentMain.java`, `CardMain.java` (Tony Liu), `Plaisante.java` (CS 205 instructional staff) |

Each part folder also has my design sketches (PDF) and, for part 2, a short `REFLECTION.md` on what I learned.

## Running

The projects need the Processing core library (`processing-core.jar`) on the classpath, and PA1 also needs the Processing Sound library (`sound.jar`, `jsyn.jar`). The library jars are not included in this repository. Run `ToneMatrix` for PA1 and `Plaisante` for PA2.
