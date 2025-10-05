# Sorting-Competition-Materials-2025
Materials and results for the UMN Morris CSci 3501 sorting competition, Fall 2025

# Table of contents
* [Goal of the competition](#goal)
* [The data](#data)
* [How is the data generated](#generating)
* [How do you need to sort the data](#sortingRules)
* [Setup for sorting](#setup)
* [Submision deadlines](#deadlines)
* [Scoring](#scoring)


## Goal of the competition <a name="goal"></a>

The Sorting Competition is a multi-lab exercise on developing the fastest sorting algorithm for a given type of data. By "fast" we mean the actual running time and not the Big-Theta approximation. The solutions are developed in Java and will be ran on a single processor.

## The data  <a name="data"></a>
The task is to implement the following sorting:
   * The data file has between 1000 and 5000000 strings of 0s and 1s of the same length, followed by another string of the same format that we call the *target* string. The target string is not a part of the data to be sorted. 
   * Two strings `str1` and `str2` in the data are compared as follows:
       * First we measure the distance between each of the two strings and the target string, measured as the number of different bits at the same position. For example, if the target string is 0101010101, then the string 1001100111 has the distance 5. If `str1` has a smaller distance to the target string than `str2`, then `str` is considered smaller than `str2`. Likewise if `str2` has the smaller distance than `str2` is smaller.
       * If the two distances to the target string are the same then we compare the two strings by the values of the binary number that they represent. For example, the strings 1001010101 and 0110010101 both have a 1 bit difference from the target string 0101010101, but 1001010101 represents a larger number, so it is considered larger. 
       
The data is generated in the following way (see DataGenerator for details): 
   * The data generator takes three parameters: the file name to write the output to, the string length, and the number of items to sort (not including the target string).  
   * It starts with a randomly chosen string of the given length and then continues generating strings by choosing some number of bits (the percentage of these bits in a string is randomly chosen between `minBitsPercent` and `maxBitsPercent`; see the data generator for specific values. Then each of the chosen bits is flipped to the opposite with the probability of 1/2. 
   * There is also a small chance (1 in `resetApprox`, currently set to 500) that an entirely new random string will be generated, instead of changing the last string in a sequence. Then the sequence continues from that new string.
   * At the very end the target string is generated as a new random string and is written put at the end of the data file. 
   
         