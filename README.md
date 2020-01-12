# Segmented Sieve of Zakiya (SSoZ)

This Java source file is a multiple threaded implementation to perform an
extremely fast Segmented Sieve of Zakiya (SSoZ) to find Twin Primes <= N.

Inputs are a range values N, of 64-bits, 0 -- 2^64 - 1.
Output is the number of twin primes <= N; the last
twin prime value for the range; and the total time of execution.

Fork this project from Github.
There's no external dependencies

Original nim source file, and updates, available here:
[https://gist.github.com/jzakiya/6c7e1868bd749a6b1add62e3e3b2341e]()

Computer used :
TOSHIBA SATELLITE L875-13D Core i7-3630QM@2.4Ghz 12Go(8+4) Ram DDR3 800Mhz
Cache1 32Ko, Cache2 256Ko, Cache3 6Mo

JAVA Example :
``` Please enter an range of integer (comma or space separated):
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
```
Mathematical and technical basis for implementation are explained here:

 <https://www.academia.edu/37952623The_Use_of_Prime_Generators_to_Implement_Fast_Twin_Primes_Sieve_of_Zakiya_SoZ_Applications_to_Number_Theory_and_Implications_for_the_Riemann_Hypotheses>
 <https://www.academia.edu/7583194/The_Segmented_Sieve_of_Zakiya_SSoZ>
<https://www.scribd.com/document/266461408/Primes-Utils-Handbook>

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


