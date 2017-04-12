This repository contains an implementation of an algorithm called `FTS-TREE` which can be used to sample block leaders from a set of stakeholders stored in a Merkle tree. The algorithm builds upon an idea called follow-the-satoshi introduced in [1].

The idea is simple: The edges the Merkle tree are labelled with the amount of coins in the left and right subtree respectively. Given a psuedo-random number generator, one can randomly select a stakeholder from the tree, weighted by the amount of coins they own, by traversing the tree down to a leaf node, containing one of the stakeholders. Each stakeholder controls a number of coins, and a private key used to sign blocks.

![An example of a stake tree with eight stakeholders.](http://i67.tinypic.com/2ish75t.jpg)

The image above shows a Merkle tree with eight stakeholders (yellow nodes), controlling a total of 95 coins. The nodes highlighted in green are the nodes visited after an execution of `FTS-TREE` where stakeholder A<sub>4</sub> was chosen as the next block leader.

The path chosen by `FTS-TREE` constitutes a Merkle proof which can be used to prove that the stakeholder was chosen correctly. This Merkle proof can then be put into the block header of the block and other blockchain nodes can use the Merkle root hash of the tree to verify the block.

This makes it possible to prune away old transactions and still be able to verify old blocks, enabling a lightweight blockchain scheme based on proof of stake.

It is fairly easy to prove that `FTS-TREE` is fair. More precisely; given a Merkle tree with <a href="https://www.codecogs.com/eqnedit.php?latex=N" target="_blank"><img src="https://latex.codecogs.com/gif.latex?N" title="N" /></a> stakeholders, containing a total of <a href="https://www.codecogs.com/eqnedit.php?latex=\sum_{i=1}^{N}{x_i}" target="_blank"><img src="https://latex.codecogs.com/gif.latex?\sum_{i=1}^{N}{x_i}" title="\sum_{i=1}^{N}{x_i}" /></a> coins, `FTS-TREE` selects the *k*:th stakeholder <a href="https://www.codecogs.com/eqnedit.php?latex=1&space;\le&space;k&space;\le&space;N" target="_blank"><img src="https://latex.codecogs.com/gif.latex?1&space;\le&space;k&space;\le&space;N" title="1 \le k \le N" /></a> controlling <a href="https://www.codecogs.com/eqnedit.php?latex=x_k" target="_blank"><img src="https://latex.codecogs.com/gif.latex?x_k" title="x_k" /></a> coins with probability <a href="https://www.codecogs.com/eqnedit.php?latex=\frac{x_k}{\sum_{i=1}^{N}{x_i}}" target="_blank"><img src="https://latex.codecogs.com/gif.latex?\frac{x_k}{\sum_{i=1}^{N}{x_i}}" title="\frac{x_k}{\sum_{i=1}^{N}{x_i}}" /></a>.

[1] Bentov, Iddo, et al. "Proof of Activity: Extending Bitcoin's Proof of Work via Proof of Stake [Extended Abstract] y." ACM SIGMETRICS Performance Evaluation Review 42.3 (2014): 34-37.
