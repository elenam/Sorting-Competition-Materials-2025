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
       * First we measure the distance between each of the two strings and the target string, measured as the number of different bits at the same position. For example, if the target string is 0101010101, then the string 1001100111 has the distance 5. If `str1` has a smaller distance to the target string than `str2`, then `str1` is considered smaller than `str2`. Likewise if `str2` has the smaller distance then `str2` is smaller.
       * If the two distances to the target string are the same then we compare the two strings by the values of the binary number that they represent. For example, the strings 1001010101 and 0110010101 both have a 1 bit difference from the target string 0101010101, but 1001010101 represents a larger number, so it is considered larger. 
       
The file [Group0.java](src/Group0.java) provides a Comparator that implements this comparison; it includes some tests (the call to the tests is commented out in main - feel free to uncomment). However, note that this is a slow implementation. Your goal is to write your own sorting that runs faster than Group0 and produces the same result. 
       
The range of parameters for data sorting is as follows: 
   * The length of the string is at least 20 and at most 120.
   * The number of strings to sort is at least 1000 and at most 5000000.      
       
### Data generation <a name="generating"></a>
The data is generated in the following way (see [DataGenerator](src/DataGenerator.java) for details): 
   * The data generator takes three parameters: the file name to write the output to, the string length, and the number of items to sort (not including the target string).  
   * It starts with a randomly chosen string of the given length and then continues generating strings by choosing some number of bits (the percentage of these bits in a string is randomly chosen between `minBitsPercent` and `maxBitsPercent`; see the data generator for specific values. Then each of the chosen bits is flipped to the opposite with the probability of 1/2. 
   * There is also a small chance (1 in `resetApprox`, currently set to 500) that an entirely new random string will be generated, instead of changing the last string in a sequence. Then the sequence continues from that new string.
   * At the very end the *target* string is generated as a new random string and is written at the end of the data file. 
   
The data is written to the data file in the order in which it is generated which creates potential opportunities to take advantage of data sequences. 

The ranges of the parameters are as follows:
   * The minimum percentage of bits selected for a possible change (`minBitsPercent`) is between 1% and 10%. 
   * The maximum percentage of bits selected for a possible change (`maxBitsPercent`) is between 10% and 100%. Note that 100% means that approximately a half of all bits will be changed. 
   * The parameter that controls the chance of reset `resetApprox` is between 100 and 10000. 

When given a file to sort, you will not know these parameters. However, you may approximate them based on observing the data as it's being read. See the setup below for details. 

## Setup for sorting <a name="setup"></a>

The file [Group0.java](src/Group0.java) provides a template for the setup for your solution. Your Java file will be called `GroupN.java`, where `N` is the group number that is assigned to your group. The template class runs the sorting method once before starting the timing so that [JVM warmup](https://www.ibm.com/developerworks/library/j-jtp12214/index.html) takes place outside of the timed sorting. It also pauses for 10ms before the actual test to let any leftover I/O or garbage collection finish. Since the warmup and the actual sorting are done on the same array (for no reason other than simplicity), the array is cloned from the same input data. 

The data reading, the array cloning, the warmup sorting, and writing out the output are all outside of the timed portion of the method, and thus do not affect the total time. 

The primary task is to come up with your own sorting algorithm, thus you will be modifying `sort` method. You can change it in any way you want - you can use your own Comparator, or no Comparator at all, you can create your own classes and use them in your sorting, etc. You may use a different type to store the data as your are sorting it. 

Here are the specifics: 
   * The `sort` method must take an array of strings and a target string. 
   * The method can sort in place (thus be `void`) as it is now, or you may return an array (not an Array list) of another type, including your own.

If you are returning an array, the following rules have to be followed:
* Your `sort` method return type needs to be changed to whatever  array you are returning. Consequently you would need to change the call in `main` to store the resulting array. 
* Your return type has to be an array (not an ArrayList!) and it has to have one element per element of the original array. That element (or its field) must be printed as is into the resulting file, no processing should be needed as they are being printed. Use of `printf` in formatting is ok. For example, you *may not* return the numbers as arrays of characters, and then at the printing time use a loop to print them character-by-character. This conversion should be done in the sorting method. 

 ### Other restrictions ###
   * Any memory allocated by your sorting method and any methods called from it must be allocated only for the duration of one round of sorting. All of the data must be recomputed on a subsequent sorting. Be especially careful with static methods, classes, or variables - they are computed once for all instances. Make sure that there is nothing left from the JVM warmup run that could be used in the actual sorting. 
   * As you are reading the data, you may collect information about the data in global variables here, as long as the total memory is constant (doesn't get depend on the data, except the string length) and no more than 1000 * L, where L is the string length. 
   * Your program will be executed on a single core (see details below), so even if you are using multithreading all the threads will be executed on the same core. However, use of multithreading is allowed, as long as you spawn no more than 4 threads and the number of threads is always the same and doesn't depend on the data.  
     
As always, definitely ask questions if you are not sure if your code is following the rules. 

## Scoring <a name="scoring"></a>

Your program needs to sort correctly and finish without throwing an exception. The correctness of sorting is determined by `diff` with the result of sorting by `Group0`. Your program must print only the time. Printing other data is considered an error, so make sure to comment out any debugging prints before submitting.  

Your program needs to follow all the rules. If in doubt, *definitely ask*. Often rules are clarified based on specific questions. After the final round of the competition, when all the code is revealed, there is a correctness code review: each group is assigned another group to review. If any rule violations are found, the group is disqualified. 

The programs are tested on a few (between 1 and 3) data sets. For each data set each group's program is run three times, the median value is recorded. The groups are ordered by their median score for each data file and assigned places, from 1 to N. 

The final score is given by the sum of places for all data sets. If the sum of places is equal for two groups, the sum of median times for all the runs resolves the tie. So if one group was first for one data set and third for the other one (2 sets total being run), it scored better than a group that was third for the first data set and second for the other. However, if one group was first for the first set and third for the other one, and the second group was second in both, the sum of times determines which one of them won since the sum of places is the same (1 + 3 = 2 + 2). 

If a program has a compilation or a runtime error, doesn't sort correctly, or prints anything other than the total time in milliseconds, it gets a penalty of 1000000ms for that test run. 

## System specs <a name="specs"></a>

The language used is Java that's installed in the CSci lab.

I will post a script for running the competition (with a correctness check) with the results of the first preliminary competition, but for now a couple of things to know: run your program out of `/tmp` directory to avoid overhead of communications with the file server, and pin your program to a single core, i.e. run it like this:
``taskset -c 0 java GroupN``

##  Submission deadlines <a name="deadlines"></a>
You will have lab time on October 9 and October 16 to work on the assignment. Of course, you can also work out of class time.  

The first preliminary competition will be run on Friday October 10th in the classroom (due Thursday October 9th at the end of the day). The purpose of it is mostly to check the correctness and to get a sense for your timing. 

The second preliminary round will be run in the classroom on Monday October 20 via ssh (due Sunday Oct 19 at 11:59pm).   

The final competition will be on Thursday October 23rd in the lab (due Wed Oct 22nd)

The dates for other related assignments (code review and presentations) will be announced later. 





