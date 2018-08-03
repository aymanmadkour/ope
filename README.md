# Order-Preserving Encryption
This is project contains Java implementations of two order-preserving encryption algorithms.

## Fast OPE using Uniform Distribution Sampling
This OPE scheme was introduced by Yong Ho Hwang et al. The basic idea is that each bit of the plaintext is assigned a pseudorandom value (determined by the key). Then, for each bit of the plaintext, if the bit is 0, the corresponding pseudorandom value is subtracted. If the bit is 1, the value is added.

## Modular OPE
Modular OPE (MOPE) was suggested by Boldyreva et al. It is not an OPE scheme by itself, but an enhancement that can be applied to an existing OPE scheme. The idea is to add a modular shift to the value being encrypted before encrypting it, thus making certain types of inference attacks more difficult.

## Data Preprocessing
For the concept of "order" to make any sense, there must be a consistent way of comparing any two pieces of data and determine which one comes first, and which one comes second.

This applies to numeric data, including signed and unsigned integers, as well as floating-point numbers. This also applies to other forms of data, such as text strings.

OPE schemes, however, support only one type of data: unsigned integers. Order comparison in OPE is simply a matter of comparing two bit strings, bit-by-bit, from the most significant bit to the least significant bit, until a bit which is different in the two bit strings is found. The bit string having a 0 bit is considered smaller than the one having a 1 bit. This property will be referred to as being "bitwise-ordered".

OPE schemes are designed in such a way as to maintain this property: if two plain values have a specific bitwise order, their corresponding cipher values will have the same bitwise order.

This property does not apply to data types other than unsigned integers. For example, in signed integers, the most significant bit is the sign, where 0 means positive and 1 means negative. This is the opposite of what we want, since negative numbers are smaller than positive numbers. In other words, standard unsigned integer encoding is not bitwise-ordered.

For OPE to be of practical value, it is important to make sure that all data types being encrypted use an OPE-friendly bitwise-ordered encoding. These encodings are provided by ope.util.Encoder class.

## References
* Boldyreva, A., Chenette, N., & O’Neill, A. (2011, August).
Order-preserving encryption revisited: Improved security analysis and alternative solutions.
In Annual Cryptology Conference (pp. 578-595). Springer, Berlin, Heidelberg.
https://link.springer.com/content/pdf/10.1007/978-3-642-01001-9_13.pdf

* Hwang, Y. H., Kim, S., & Seo, J. W. (2015, October).
Fast order-preserving encryption from uniform distribution sampling.
In Proceedings of the 2015 ACM Workshop on Cloud Computing Security Workshop (pp. 41-52). ACM.
https://dl.acm.org/citation.cfm?id=2808431
