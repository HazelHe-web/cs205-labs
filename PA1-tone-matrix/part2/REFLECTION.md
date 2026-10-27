PA 1: Tone Matrix

Name: Shiman He

1. Approximately how much time did you spend on the final PA 1 submission?
About 6 hours.

2. What were the most challenging portion(s) of the assignment?
The most challenging part was the draw() method. I had to think about all the cases for the fill color (lit, unlit in the active column, unlit and not active) and make sure the note only plays when the cell is lit, in the active column, and not already playing. Converting between pixel coordinates and row/col indices was also confusing at first, because x goes with the column and y goes with the row.

3. What is one thing you have a better understanding of after completing this assignment and going through the class content?
I understand 2D arrays much better now. Using two parallel 2D arrays (lightGrid and audioSampleGrid) that line up index for index helped me see how the same [row][col] can describe one cell in different ways.

4. Do you have any follow-up questions about concepts that you'd like to explore further?
How could I add mouseDragged() so I can paint across many cells at once, and how could the tempo be changed while the program is running?