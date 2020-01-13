# Segmented Sieve of Zakiya (SSoZ)

This Java source file is a multiple threaded implementation to perform an
extremely fast Segmented Sieve of Zakiya (SSoZ) to find Twin Primes <= N.

Inputs are a range values N, of 64-bits, 0 -- 2^64 - 1.
Output is the number of twin primes <= N; the last
twin prime value for the range; and the total time of execution.

Fork this project from Github.
There's no external dependencies

Computer used :
TOSHIBA SATELLITE L875-13D Core i7-3630QM@2.4Ghz 12Go(8+4) Ram DDR3 800Mhz
Cache1 32Ko, Cache2 256Ko, Cache3 6Mo

JAVA Example :
``` Please enter an range of integer (comma or space separated): 
0 2e11
 Max threads = 8
Using Prime Generator parameters for given Pn 13
segment size = 524288 resgroups; seg array is [1 x 8192] 64-bits
twinprime candidates = 9890110395 ; resgroups = 6660007
each 1485 threads has nextp[2 x 37493] array
setup time = 0.111 secs
perform twinprimes ssoz sieve with s=6
1485 of 1485 threads done
sieve time = 23.793 secs
last segment = 368551 resgroups; segment slices = 13
total twins = 424084653; last twin = 199999999890+/-1
total time = 23.904 secs
```

Original nim source file, and updates, available here:
https://gist.github.com/jzakiya/6c7e1868bd749a6b1add62e3e3b2341e
Original d source file, and updates, available here:
https://gist.github.com/jzakiya/ae93bfa03dbc8b25ccc7f97ff8ad0f61
Original rust source file, and updates, available here:
https://gist.github.com/jzakiya/b96b0b70cf377dfd8feb3f35eb437225

Mathematical and technical basis for implementation are explained here:
https://www.academia.edu/37952623The_Use_of_Prime_Generators_to_Implement_Fast_Twin_Primes_Sieve_of_Zakiya_SoZ_Applications_to_Number_Theory_and_Implications_for_the_Riemann_Hypotheses
https://www.academia.edu/7583194/The_Segmented_Sieve_of_Zakiya_SSoZ
https://www.academia.edu/19786419/PRIMES-UTILS_HANDBOOK

## Releases

* 3A 
* 1A 1B (Actual)
* 1

## Getting Started

Clone the repository, create a plain Java project importing it, make sure that 'src' is the source folder of your project.
Or simply use the gist https://gist.github.com/Pascal66/4d4229e88f4002641ddcaa5eccd0f6d5
You will need Java 8 or higher for the project to compile.

## Remarks

Java version 0.0.21 - Pascal Péchard -- pascal at priveyes dot net
<p>Version Date: 2020/01/12

## Authors

Copyright (c) 2017-20 Jabari Zakiya -- jzakiya at gmail dot com

## License

This code is provided free and subject to copyright and terms of the
GNU General Public License Version 3, GPLv3, or greater.
License copy/terms are here:  http://www.gnu.org/licenses/
- see the [LICENSE](LICENSE) file for details

## Credits


