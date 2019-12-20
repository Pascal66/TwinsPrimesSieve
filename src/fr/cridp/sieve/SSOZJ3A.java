package fr.cridp.sieve;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

/**
This Java source file is a multiple threaded implementation to perform an
extremely fast Segmented Sieve of Zakiya (SSoZ) to find Twin Primes <= N.

Inputs are single values N, of 64-bits, 0 -- 2^64 - 1.
Output is the number of twin primes <= N; the last
twin prime value for the range; and the total time of execution.

Parameter tuning would be needed to optimize for other hardware systems (ARM, PowerPC, etc).

Create a new java project from Eclipse (or equivalent)
There's no external dependencies
Copy this file in the src folder of the newly created project
(Change the package name according the name of your project)
OR
javac SSOZJ3A.java
java SSOZJ3A
OR JDK 11
java SSOZJ3A.java

 Run as Java application, and enter a N value when asked in console.

This java source file, and updates, will be available here:
https://gist.github.com/Pascal66/4d4229e88f4002641ddcaa5eccd0f6d5

Not necessary (no real improvment before 1E12) to use jvm option like:
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

This code is provided free and subject to copyright and terms of the
GNU General Public License Version 3, GPLv3, or greater.
License copy/terms are here:  http://www.gnu.org/licenses/

Copyright (c) 2017-20 Jabari Zakiya -- jzakiya at gmail dot com
Java version 0.0.20 for fun - Pascal Pechard
Version Date: 2019/12/20
 */

public class SSOZJ3A {

	static final BigInteger TWO = ONE.add(ONE);
	static final BigInteger THREE = TWO.add(ONE);

	static long KB = 0L;               	// segment size for each seg restrack
	static BigInteger start_num;      	// lo number for range
	static BigInteger end_num;      	// hi number for range
	static BigInteger Kmax = ZERO;      // number of resgroups to end_num
	static BigInteger Kmin;      		// number of resgroups to start_num
	static ArrayDeque<Long> primes; 	// list of primes r1..sqrt(N)
	static long[] cnts; 				// hold twin primes counts for seg bytes
	static long[] lastwins; 			// holds largest twin prime <= num in each thread
	static BigInteger modpg;       		// PG's modulus value
	static long res_0;       			// PG's first residue value
	static LinkedList<Long> restwins; 	// PG's list of twinpair residues
	static long[] resinvrs; // PG's list of residues inverses
	static int Bn;       				// segment size factor for PG and input number
	static int s;       				// 0|3 for 1|8 resgroups/byte for 'small|large' ranges

	// Global array used to count number of primes in each 'seg' byte.
	// Each value is number of '0' bits (primes) for values 0..255.
	private final static short[] pbits = {
			8,7,7,6,7,6,6,5,7,6,6,5,6,5,5,4,7,6,6,5,6,5,5,4,6,5,5,4,5,4,4,3
			,7,6,6,5,6,5,5,4,6,5,5,4,5,4,4,3,6,5,5,4,5,4,4,3,5,4,4,3,4,3,3,2
			,7,6,6,5,6,5,5,4,6,5,5,4,5,4,4,3,6,5,5,4,5,4,4,3,5,4,4,3,4,3,3,2
			,6,5,5,4,5,4,4,3,5,4,4,3,4,3,3,2,5,4,4,3,4,3,3,2,4,3,3,2,3,2,2,1
			,7,6,6,5,6,5,5,4,6,5,5,4,5,4,4,3,6,5,5,4,5,4,4,3,5,4,4,3,4,3,3,2
			,6,5,5,4,5,4,4,3,5,4,4,3,4,3,3,2,5,4,4,3,4,3,3,2,4,3,3,2,3,2,2,1
			,6,5,5,4,5,4,4,3,5,4,4,3,4,3,3,2,5,4,4,3,4,3,3,2,4,3,3,2,3,2,2,1
			,5,4,4,3,4,3,3,2,4,3,3,2,3,2,2,1,4,3,3,2,3,2,2,1,3,2,2,1,2,1,1,0};

	/**
	 * Create constant parameters for given PG at build time.
	 * Using BigInteger permit Optimized gcd and ModInverse
	 * At build time, both version (Long and BigInteger are created) depend of inputs
	 * This give loosing time at setup and benefit for parallel computation
	 * @param prime int
	 */
	private static void genPGparameters(int prime){
		System.out.println("generating parameters for P "+ prime);
		final long[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23};
		modpg = ONE;  res_0 = 0L;
		for (long prm : primes){ res_0 = prm ;
			if (prm > prime) break; modpg = modpg.multiply(BigInteger.valueOf(res_0));
		}

		LinkedList<Long> restwins = new LinkedList<>(); 			// save upper twin pair residues here
		long[] inverses = new long[modpg.intValue()];				// save PG's residues inverses here
		BigInteger pc = THREE.add(TWO); int inc = 2; BigInteger res = ZERO;
		while (pc.compareTo(modpg.divide(TWO)) < 0) {       		// find a residue, then modular complement
			if (modpg.gcd(pc).equals(ONE)) {          				// if pc a residue
				final BigInteger pc_mc = modpg.subtract(pc);		// create its modular complement
				Integer inv_r = pc.modInverse(modpg).intValue();	// modinv(pc, modpg);  // compute residues's inverse
				inverses[pc.subtract(TWO).intValue()]= inv_r;   	// save its inverse
				inverses[inv_r-2] = pc.intValue();     				// save its inverse inverse
				inv_r = pc_mc.modInverse(modpg).intValue(); 		// compute residues's complement inverse
				inverses[pc_mc.subtract(TWO).intValue()] = inv_r;	// save its inverse
				inverses[inv_r-2] = pc_mc.intValue();   			// save its inverse inverse
				if (res.add(TWO).equals(pc)){
					restwins.add(pc.longValue());
					restwins.add(pc_mc.add(TWO).longValue());} 		// save hi_tp residues
				res = pc;
			}
			pc = BigInteger.valueOf(pc.longValue() + inc); inc ^= 0b110;
		}
		restwins.sort(Comparator.naturalOrder());
		// last residue is last hi_tp
		restwins.add(modpg.add(ONE).longValue());
		inverses[modpg.subtract(ONE).intValue()]= ONE.intValue();
		// last 2 residues are self inverses
		inverses[modpg.subtract(THREE).intValue()]= modpg.subtract(ONE).intValue();

		new PGparam(modpg, res_0, restwins, inverses);
	}

	/**
	 * Here we store all we precompute before
	 * This doesnt penalize nothing and avoid passing parameters
	 * Migration en cours...
	 */
	static class PGparam{
		public static Long Lmodpg;
		public static Long Lkmin;
		public static Long Lkmax;
		public static Long Lstart;
		public static Long Lend;
		public static int primesSize;
		static int segByteSize = 0;

		public PGparam(BigInteger modpg, Long res_0, LinkedList<Long> restwins, long[] inverses) {
			SSOZJ3A.modpg = modpg;
			SSOZJ3A.res_0 = res_0;
			SSOZJ3A.restwins = restwins;
			SSOZJ3A.resinvrs = inverses;
			Lmodpg = modpg.longValueExact();

			Kmin = start_num.subtract(TWO).divide(modpg).add(ONE);	// number of resgroups to start_num
			Kmax = end_num.subtract(TWO).divide(modpg).add(ONE);	// number of resgroups to end_num

			Lkmin= Kmin.longValue();
			Lkmax = Kmax.longValue();

			}
	}

	/**
	 * Select at runtime best PG and segment size factor to use for input value.
	 * These are good estimates derived from PG data profiling. Can be improved.
	 *
	 * @param start_range BigInteger
	 * @param end_range bigInteger
	 */
	private static void selectPG(BigInteger start_range, BigInteger end_range) {
		final BigInteger range = end_range.subtract(start_range);
		int pg = 3;
		if (end_range.compareTo(BigInteger.valueOf(49L)) < 0) {
			Bn = 1;
//			pg = 3;
		} else if (range.compareTo(BigInteger.valueOf(24_000_000L))<0) {
			Bn = 16;
			pg = 5;
		} else if (range.compareTo(BigInteger.valueOf(1_100_000_000L))<0) {
			Bn = 32;
			pg = 7;
		} else if (range.compareTo(BigInteger.valueOf(35_500_000_000L))<0) {
			Bn = 64;
			pg = 11;
		} else if (range.compareTo(BigInteger.valueOf(15_000_000_000_000L))<0) {
			pg = 13;
			if (range.compareTo(BigInteger.valueOf(7_000_000_000_000L)) > 0) {
				Bn = 384;
			} else if (range.compareTo(BigInteger.valueOf(2_500_000_000_000L)) > 0) {
				Bn = 320;
			} else if (range.compareTo(BigInteger.valueOf(250_000_000_000L)) > 0) {
				Bn = 196;
			} else {
				Bn = 128;
			}
		} else {
			Bn = 384;
			pg = 17;
		}
		// Set value for 'small' or 'large' ranges to opt sieving
		s = range.compareTo(BigInteger.valueOf(100_000_000_000L))<0 ? 0 : 3;
		genPGparameters(pg);
	}
	/**
	 * Compute the primes r1..sqrt(input_num) and store in global 'primes' array.
	 * Any algorithm (fast|small) is usable. Here the SoZ for P5 is used.
	 * @param val bigInteger
	 */
	private static void sozpg(BigInteger val) {
		final int md = 30;             // P5's modulus value
		final int rscnt = 8;           // P5's residue count

		final int[] res ={7,11,13,17,19,23,29,31}; // P5's residues list
		final int[] posn = {0,0,0,0,0,0,0,0,0,1,
				0,2,0,0,0,3,0,4,0,0,
				0,5,0,0,0,0,0,6,0,7};

		final long sqrtN = Bsqrt(val).longValue(); 	// Biginteger sqrt of sqrt(input value)
		final int kmax = val.subtract(BigInteger.valueOf(7L)).divide(BigInteger.valueOf(md)).add(ONE).intValue();   // number of resgroups to input value

		// byte array of prime candidates init '0'
		short[] prms = new short[kmax];
		// init residue parameters
		int modk = 0; int r = -1; int k = 0;
		// mark the multiples of the primes r1..sqrtN in 'prms'
		while (true) {
			r++;
			if (r == rscnt) { r = 0; modk += md; k++; }
			// skip pc if not prime
			if ((prms[k] & (1 << r)&0XFF) != 0) continue;
			// if prime save its residue value
			final int prm_r = res[r];
			// numerate the prime value
			final int prime = modk + prm_r;
			// we're finished when it's > sqrtN
			if (prime > sqrtN) break;
			// mark prime's multiples in prms
			for (int ri : res) {
				// compute cross-product for prm_r|ri pair
				final int prod = prm_r * ri - 2;
				// bit mask for prod's residue
				final int bit_r =(1 << posn[(int) mod(prod , md)])&0XFF;
				// 1st resgroup for prime mult
				int kpm = k * (prime + ri) + prod / md;
				while (kpm < kmax){ prms[kpm] |= bit_r; kpm += prime; }
			}
		}

		//  prms now contains the nonprime positions for the prime candidates r1..N
		//  extract primes into global var 'primes'

		// create empty dynamic array for primes
		// Here we use the ArrayDeQueue because First & Last are O(1) and iterate like any others: O(n)
		 primes = new ArrayDeque<Long>();
		// for each resgroup
		IntStream.range(0, kmax).forEach(km->{
			int[] ri = {0};
			// extract the primes in numerical order
			for (int res_r : res) {
				if ((prms[km] & (1 << ri[0]++)&0XFF) == 0)
					primes.add((long) md * km + res_r);
			}
		});
		while (primes.getFirst() < res_0) primes.pollFirst();
		while (primes.getLast() > val.longValue()) primes.pollLast();
	}

	/**
	 * Print twinprimes for given twinpair for given segment slice.
	 * Primes will not be displayed in sorted order, collect|sort later for that.
	 * @param Kn Long
	 * @param Ki Long
	 * @param indx int
	 * @param seg short[]
	 */
	private static void printprms(Long Kn, Long Ki, int indx, short[] seg) {
		// base value of 1st resgroup in slice
		long modk = Ki * PGparam.Lmodpg;
		// for upper twinpair residue value
		final long r_hi = restwins.get(indx);
		// for each byte of resgroups in slice
		for (int k = (int) (Kn - 1 >>> 3); k > -1; k--)
			// extract the primes for each resgroup
			for (int r = 7; r > -1; r--)  {
				if ((seg[k] | (1 << r)&0XFF) == 0 && modk + r_hi <= PGparam.Lend)
					// print twinprime mid val on a line
					System.out.println(modk + r_hi - 1);
				// set base value for next resgroup
				modk += PGparam.Lmodpg;
			}
	}

	/**
	 * Compute 1st prime multiple resgroups for each prime r1..sqrt(N)
	 * and store consecutively as lo_tp|hi_tp pairs for their restracks.
	 *
	 * @param hi_r twin pair residues
	 * @param prime Long
	 * @param kmin long
	 * @return nextp 1st mults array for twin pair
	 */
	private static long[] nextp_init(long hi_r, Long prime, long kmin) {
		// upper|lower twin pair residues
		long r_hi = hi_r; long r_lo = r_hi - 2;
			long[] modDiv = floorDivAndMod(prime - 2, PGparam.Lmodpg);
			// find the resgroup it's in
			long k = modDiv[0]; //(prime - 2) / PGparam.Lmodpg;
			// and its residue value
			long r = modDiv[1] + 2; //mod(prime - 2, PGparam.Lmodpg) + 2;
			// and its residue inverse
			long r_inv = resinvrs[(int) modDiv[1]/*(r - 2)*/];
			// compute the rlow for r for lo_tp
			long ro = mod(r_lo * r_inv - 2, PGparam.Lmodpg) + 2;
			long ko = (k * (prime + ro) + (r * ro - 2) / PGparam.Lmodpg);
			if (ko < kmin) {							// if 1st mult index < start_num's
				ko = mod(kmin - ko, prime);			// how many indices short is it
				if (ko > 0) ko = prime - ko;			// adjust index value into range
			} else ko -= kmin;							// else here, adjust index if it was >
			// compute the rright for r for hi_tp
			long ri = mod(r_hi * r_inv - 2, PGparam.Lmodpg) + 2;
				long ki = k * (prime + ri) + (r * ri - 2) / PGparam.Lmodpg;
				if (ki < kmin) {						// if 1st mult index < start_num's
					ki = mod(kmin - ki, prime);		// how many indices short is it
					if (ki > 0) ki = prime - ki;		// adjust index value into range
				} else ki -= kmin;						// else here, adjust index if it was >
			return new long[]{ko, ki};
	}

	/**
	 * Perform in a thread, the ssoz for a given twinpair, for Kmax resgroups.
	 * First create|init 'nextp' array of 1st prime mults for given twin pair,
	 * (stored consequtively in 'nextp') and init seg byte array for KB resgroups.
	 * For sieve, mark resgroup bits to '1' if either twinpair restrack is nonprime,
	 * for primes mults resgroups, and update 'nextp' restrack slices acccordingly.
	 * <p>
	 * Find last twin prime|sum for range, store in their arrays for this twinpair.
	 * Can optionally compile to print mid twinprime values generated by twinpair.
	 * Uses optimum segment sieve structure for 'small' and 'large' range values.
	 *
	 * @param indx
	 * @param r_hi long
	 */
	private static void twins_sieve(int indx, long r_hi) {
		long kmin = PGparam.Lkmin-1;
		long kmax = PGparam.Lkmax;
		// init twins cnt|1st resgroup for slice
		long sum = 0; long Kn = KB;
		long hi_tp = 0;  			// max tp|resgroup, Kmax for slice

		// seg byte array for Kb resgroups
		short[] seg = new short[PGparam.segByteSize];
		// 1st mults array for twin pair
		long[] nextp = new long[PGparam.primesSize << 1];
		// ensure lo tps in range
		if (kmin * PGparam.Lmodpg + (r_hi - 2) < PGparam.Lstart) kmin++;
		// and hi tps in range
		if ((kmax-1) * PGparam.Lmodpg + (r_hi) > PGparam.Lend) kmax--;

		long K1 = kmin;
		int j = 0;
		// for Kn resgroup size slices upto Kmax
		while (kmin < kmax) {
			// set last slice resgroup size
			if (KB > kmax - kmin) Kn = kmax - kmin;
			for (Long prime : primes) {
				// if 1st segment init nextp array vals
				if (kmin == K1) {
					long[] nextp_init = nextp_init(r_hi, prime, kmin);
					nextp[j << 1] = nextp_init[0];
					nextp[j << 1 | 1] = nextp_init[1];
				}
				// for lower twin pair residue track
				// starting from this resgroup in seg
				int k = (int) nextp[j << 1];
				// mark primenth resgroup bits prime mults
				while (k < Kn) {
					if (s > 0) seg[k >>> 3] |= (1 << (k & 7))&0XFF;
					else seg[k] |= 1;
					// set next prime multiple resgroup
					k += prime;
				}
				// save 1st resgroup in next eligible seg
				// for upper twin pair residue track
				nextp[j << 1] = k - Kn;
				// starting from this resgroup in seg
				// mark primenth resgroup bits prime mults
				k = (int) nextp[j << 1 | 1];
				while (k < Kn) {
					if (s > 0) seg[k >>> 3] |= (1 << (k & 7))&0XFF;
					else seg[k] |= 1;
					// set next prime multiple resgroup
					k += prime;
				}
				nextp[j << 1 | 1] = k - Kn;
				j++;
			}
			int upk = (int) (Kn - 1);
			// (not ((2 shl ((Kn-1) and 7)) - 1)).uint8
			if (s > 0) seg[upk >>> 3] |= ((-(2 << (upk & 7)))&0XFF);
			// init seg twin primes count then find seg sum
			int cnt = 0;
			if (s > 0) for (int k = upk >>> 3; k > -1; k--) cnt += pbits[seg[k]];
			else for (int k = upk; k > -1; k--) if (seg[k] == 0) cnt++;

			// if segment has twin primes
			if (cnt > 0) {
				// add the segment count to total count
				sum += cnt;
				// from end of seg, count backwards to largest tp
				if (s > 0) while ((seg[upk >>> 3] & (1 << (upk & 7))&0XFF) != 0) upk--;
				else while (seg[upk] != 0) upk--;
				// numerate its full resgroup value
				hi_tp = kmin + upk;
			}
			//printprms(Kn, kmin, indx, seg);  // optional: display twin primes in seg
			// set 1st resgroup val of next seg slice
			kmin += KB;
			// set all seg byte bits to prime
			Arrays.fill(seg, (short) 0);
			j = 0;
		}
		// numerate largest twin prime in segs
		hi_tp = r_hi > PGparam.Lend ? 0 : hi_tp * PGparam.Lmodpg + r_hi;

		// store final seg tp value
		lastwins[indx] = (sum == 0 ? 1 : hi_tp);
		// sum for twin pair
		cnts[indx] = sum;                     		}

	/**
	 * Main routine to setup, time, and display results for twin primes sieve.
	 */
	private static void twinprimes_ssoz() {
		System.out.println(" Max threads = "+ (countProcessors()));
		// start timing sieve setup execution
		long ts = epochTime();

		selectPG(start_num, end_num);     		 // select PG and seg factor Bn for input range
		final int pairscnt = restwins.size();    // number of twin pairs for selected PG
		cnts = new long[pairscnt];     			 // array to hold count of tps for each thread
		lastwins = new long[pairscnt];			 // array to hold largest tp for each thread

		// number of range resgroups, at least 1
		final Long range = PGparam.Lkmax - PGparam.Lkmin + 1;
		int n = range.compareTo((37_500_000_000_000L))<0 ? 4
				: range.compareTo((975_000_000_000_000L))<0 ? 6 : 8;
		if (s == 0) n = 1;
		// set seg size to optimize for selected PG
		final long B = (long) Bn * 1024 * n;
		// segments resgroups size
		KB = Math.min(range, B);
		// Doing that one time is enough
		PGparam.segByteSize = (int) ((KB-1 >>>s ) + 1);

		System.out.println("each thread segment is ["+ 1+ " x "+ PGparam.segByteSize+ "] bytes array");

		// This is not necessary for running the program but provides information
		// to determine the 'efficiency' of the used PG: (num of primes)/(num of pcs)
		// Maximum number of twinprime pcs
		final long maxpairs = range * pairscnt;
		System.out.println("twinprime candidates = "+ maxpairs+ "; resgroups = "+ range);
		//   End of non-essential code.

		// Generate sieving primes <= sqrt(end_num)
		if (PGparam.Lend < 49L) primes.add((5L));
		else sozpg(Bsqrt(end_num));

		PGparam.primesSize = primes.size();
		System.out.println("each "+ pairscnt+ " threads has nextp["+ 2+ " x "+ PGparam.primesSize + "] array");

		// number of twin primes in range
		long twinscnt = 0;
		// lo_range = lo_tp - 1 this isnt clear
		final long lo_range = restwins.getFirst() - 3;
		// excluded low tp values for PGs used
		for (int tp : new int[]{3, 5, 11, 17}) {
			// if 3 end of range, no twin primes
			if (end_num.equals(THREE)) break;
			//.nim version if (tp/*.uint*/ in start_num..lo_range) twinscnt += 1; 	// cnt small tps if any
			//.other version if(Math.max(start_num.longValue(), tp) == Math.min(tp, lo_range)) twinscnt+=1;
			//.d version
			if (tp >= PGparam.Lstart && tp <= lo_range) { twinscnt += 1; }
		}
		// sieve setup time
		long te = epochTime() - ts;
		System.out.println("setup time = "+ te/1e3 + " secs");
		System.out.println("perform twinprimes ssoz sieve with s="+s);

		ExecutorService stealingPool = Executors.newWorkStealingPool();

		List<Runnable> sieveTask = new ArrayList<>();
		final Callback<String> callback = System.out::print;

		AtomicInteger indx = new AtomicInteger();
		// for each twin pair row index
		for (long r_hi : restwins) {
			sieveTask.add(() -> {
				callback.on("\r"+indx.get() + " of "+ pairscnt+ " threads done");
				twins_sieve(indx.getAndIncrement(), r_hi); // sieve selected twin pair restracks
			});
		}
		// start timing ssoz sieve execution
		final long t1 = epochTime();
		// Implement parallel things
		try {
			stealingPool.submit(()->sieveTask.parallelStream().forEach(Runnable::run)).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			// when all the threads finish
			stealingPool.shutdown();
			System.out.println("\r"+indx + " of "+ pairscnt+ " threads done");
		}
		// OR Simple parallel without specific pool
		// sieveTask.parallelStream().forEach(Runnable::run);

		// find largest twin prime in range
		long last_twin = 0L;
		twinscnt += Arrays.stream(cnts).sum();
		last_twin = Arrays.stream(lastwins).max().orElse(0L);

		if (PGparam.Lend == 5L && twinscnt == 1) last_twin = 5L;
		// set number of resgroups in last slice
		long Kn = mod(range, KB);
		// if multiple of seg size set to seg size
		if (Kn == 0) Kn = KB;
		// Free memory
		cnts = null; lastwins = null;
		// sieve execution time
		long t2 = epochTime() - t1;
		System.out.println("sieve time = "+ t2/1e3 + " secs");
		System.out.println("last segment = "+ Kn+ " resgroups; segment slices = "+ ((range-1) / KB + 1));
		System.out.println("total twins = "+ twinscnt+ "; last twin = "+ (last_twin-1) + "+/-1");
		System.out.println("total time = "+ (t2 + te)/1e3 + " secs\n");
	}

	public static void main(String[] args) {
		//if (args==null)
		Scanner userInput = new Scanner(System.in).useDelimiter("[,\\s+]");
		System.out.println("Please enter an range of integer (comma or space separated): ");
		//Only BigDecimal understand scientific notation
		BigInteger stop = userInput.nextBigDecimal().toBigIntegerExact();
		BigInteger start = userInput.hasNextLine()?userInput.nextBigDecimal().toBigIntegerExact():THREE;

		userInput.close();

		if (stop.compareTo(start) < 0) {
			BigInteger tmp = start;
			start = stop;
			stop = tmp;
		}

		start = start.max(THREE);
		BigInteger end = stop.max(THREE);

		// if start_num even add 1
		start_num = start.or(ONE);
		// if end_num even subtract 1
		end_num = end.subtract(ONE).or(ONE);
		PGparam.Lstart = start_num.longValue();
		PGparam.Lend = end_num.longValue();

		twinprimes_ssoz();
	}

	private static int countProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	/** Warning, Granularity can be tens of milliseconds.*/
	private static long epochTime() {
		return System.currentTimeMillis();
	}

	/**
	 * One Efficient BigInteger sqrt, we are not using flot or double, just integer
	 * @see "https://stackoverflow.com/questions/4407839/how-can-i-find-the-square-root-of-a-java-biginteger"
	 * @param x BigInteger
	 * @return BigInteger sqrt of x
	 */
	private static BigInteger Bsqrt(BigInteger x) {
		BigInteger div = BigInteger.ZERO.setBit(x.bitLength()/2);
		BigInteger div2 = div;
		// Loop until we hit the same value twice in a row, or wind up alternating.
		for(;;) {
			BigInteger y = div.add(x.divide(div)).shiftRight(1);
			if (y.equals(div) || y.equals(div2))
				//	return y;
				return div.min(div2);
			div2 = div;
			div = y;
		}
	}

	/**
	 * According to the Java Language Spec,
	 * Java's % operator is a remainder operator, not a modulo operator.
 	 * @param x a long
	 * @param y a long
	 * @return a long modulo
	 */
	private static long mod(long x, long y)	{
// Original
//		long mod = x % y;
		// if the signs are different and modulo not zero, adjust result
//		if ((mod ^ y) < 0 && mod != 0) {
//			mod += y;
//		}
//		return mod;
		//equivalent of nimlang definition
		//return x - (Math.floorDiv(x, y) * y);
		//same as
		return Math.floorMod(x,y);
	}

	/**
	 *
	 * @param x long
	 * @param y long
	 * @return div & mod without additional division
	 */
	private static long[] floorDivAndMod(long x, long y) {
		long div = Math.floorDiv(x, y);
		long mod = x - div * y;
		return new long[]{div, mod};
	}

	/**
	 * Just a little workaround to print state of threads
	 * This isn't working in a ide console, just in command line call
	 */
	@FunctionalInterface
	public interface Callback<T> {
		void on(T event);
	}
}
