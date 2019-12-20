# Segmented Sieve of Zakiya (SSoZ)

This Java source file is a multiple threaded implementation to perform an
extremely fast Segmented Sieve of Zakiya (SSoZ) to find Twin Primes <= N.

Inputs are single values N, of 64-bits, 0 -- 2^64 - 1.
Output is the number of twin primes <= N; the last
twin prime value for the range; and the total time of execution.

Parameter tuning would be needed to optimize for other hardware systems (ARM, PowerPC, etc).

Fork this project from Github.
There's no external dependencies
 Run as Java application, and enter a N value when asked in console.
 OR Jdk 8/9
javac SSOZJ3A.java
java SSOZJ3A
 OR JDK 11
java SSOZJ3A.java

This original java source file, and updates, will be available here:
https://gist.github.com/Pascal66/4d4229e88f4002641ddcaa5eccd0f6d5

Not necessary (nor real improvment before 1E12) to use jvm option like:
 -XX:+AggressiveOpts
 -Xms4g -Xmx4g
 -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:+UseParallelGC
 -XX:+UseNUMA
 -XX:+UseCompressedOops
 -XX:+UseG1GC

 Computer used :
TOSHIBA SATELLITE L875-13D Core i7-3630QM@2.4Ghz 12Go(8+4) Ram DDR3 800Mhz
Cache1 32Ko, Cache2 256Ko, Cache3 6Mo

JAVA Example :
 Please enter an range of integer (comma or space separated):
 0 2e11
 Max threads = 8
 generating parameters for P 13
 each thread segment is [1 x 65536] bytes array
 twinprime candidates = 9890110395; resgroups = 6660007
 each 1485 threads has nextp[2 x 37493] array
 setup time = 0.11 secs
 perform twinprimes ssoz sieve with s=3
 1485 of 1485 threads done
 sieve time = 30.229 secs
 last segment = 368551 resgroups; segment slices = 13
 total twins = 424084653; last twin = 199999999890+/-1
 total time = 30.339 secs

 NIM Example :
 c:\~\nim-1.0.4>twinprimes_ssoz
 200000000000
 threads = 8
 each thread segment is [1 x 65536] bytes array
 twinprime candidates = 9890110395; resgroups = 6660007
 each 1485 threads has nextp[2 x 37493] array
 setup time = 0.000000 secs
 perform twinprimes ssoz sieve
 1485 of 1485 threads done
 sieve time = 32.507 secs
 last segment = 368551 resgroups; segment slices = 13
 total twins = 424084653; last twin = 199999999890+/-1
 total time = 32.507 secs

 Inspired from a discussion @see "https://forum.nim-lang.org/t/4950" and
Original nim source file, and updates, available here:
https://gist.github.com/jzakiya/6c7e1868bd749a6b1add62e3e3b2341e
This work isnt here to prove anything, or wich language is better or not.

Mathematical and technical basis for implementation are explained here:
https://www.scribd.com/document/395415391/The-Use-of-Prime-Generators-to-
Implement-Fast-Twin-Primes-Sieve-Of-Zakiya-SoZ-Applications-to-Number-Theory-
and-Implications-for-the-Riemann-Hypoth
https://www.scribd.com/doc/228155369/The-Segmented-Sieve-of-Zakiya-SSoZ
https://www.scribd.com/document/266461408/Primes-Utils-Handbook

Copyright (c) 2017-20 Jabari Zakiya -- jzakiya at gmail dot com
Java version 0.0.20 for fun - Pascal Pechard
Version Date: 2019/12/20

## Releases

* 
*

## Getting Started

Clone the repository, create a plain Java project importing it, make sure that 'src' is the source folder of your project, and add the jars from the lib-folder to your classpath. 
You will need Java 8 or higher for the project to compile.
There is no documentation and no support, so you should be ready to start exploring the source code.

## Remarks


## Authors

Jabari Zakiya was born and raised in Washington, DC. 
 After graduating from McKinley Tech HS, he received a BSEE from Cornell University, and then a MSEE from Ga Tech. 
 He then worked at NASA Goddard Space Flight Center (GFSC) in Greenbelt, MD for 15 years, as an engineer building space data communication systems. 
 After NASA he co-founded 3rdeye Technologies, to leverage hardware IP (intellectual property) he created around encryption algorithms. 
 Most recently, he has developed extremely fast prime sieve methods, the Sieve of Zakiya (SoZ) and Segmented Sieve of Zakiya (SSoZ). 
 He's also fluent in various programming languages, and has written various papers about programming. 
 Jabari has been teaching and tutoring math basically all his life. 
 He's formally been tutoring/teaching math in the 2000's with various tutoring companies, and freelance. 
 He's also taught a semester of calculus at a DC High School in 2008.

## License

This code is provided free and subject to copyright and terms of the
GNU General Public License Version 3, GPLv3, or greater.
License copy/terms are here:  http://www.gnu.org/licenses/
- see the [LICENSE](LICENSE) file for details

## Credits
Big thanks to
* Jabari.

