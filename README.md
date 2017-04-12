This repository contains an implementation of an algorithm called `FTS-TREE` which can be used to sample block leaders from a set of stakeholders stored in a Merkle tree. The algorithm builds upon an idea called follow-the-satoshi introduced in [1].

The idea is simple: The edges the Merkle tree are labelled with the amount of coins in the left and right subtree respectively. Given a psuedo-random number generator, one can randomly select a stakeholder from the tree, weighted by the amount of coins they own, by traversing the tree down to a leaf node, containing one of the stakeholders. Each stakeholder controls a number of coins, and a private key used to sign blocks.

![An example of a stake tree with eight stakeholders.](http://i67.tinypic.com/2ish75t.jpg)

The image above shows a Merkle tree with eight stakeholders (yellow nodes), controlling a total of 95 coins. The nodes highlighted in green are the nodes visited after an execution of `FTS-TREE` where stakeholder A<sub>4</sub> was chosen as the next block leader.

The path chosen by `FTS-TREE` constitutes a Merkle proof which can be used to prove that the stakeholder was chosen correctly. This Merkle proof can then be put into the block header of the block and other blockchain nodes can use the Merkle root hash of the tree to verify the block.

This makes it possible to prune away old transactions and still be able to verify old blocks, enabling a lightweight blockchain scheme based on proof of stake.

It is fairly easy to prove that `FTS-TREE` is fair. More precisely; given a Merkle tree with <a href="https://www.codecogs.com/eqnedit.php?latex=N" target="_blank"><img src="https://latex.codecogs.com/gif.latex?N" title="N" /></a> stakeholders, containing a total of <a href="https://www.codecogs.com/eqnedit.php?latex=\sum_{i=1}^{N}{x_i}" target="_blank"><img src="https://latex.codecogs.com/gif.latex?\sum_{i=1}^{N}{x_i}" title="\sum_{i=1}^{N}{x_i}" /></a> coins, `FTS-TREE` selects the *k*:th stakeholder <a href="https://www.codecogs.com/eqnedit.php?latex=1&space;\le&space;k&space;\le&space;N" target="_blank"><img src="https://latex.codecogs.com/gif.latex?1&space;\le&space;k&space;\le&space;N" title="1 \le k \le N" /></a> controlling <a href="https://www.codecogs.com/eqnedit.php?latex=x_k" target="_blank"><img src="https://latex.codecogs.com/gif.latex?x_k" title="x_k" /></a> coins with probability <a href="https://www.codecogs.com/eqnedit.php?latex=\frac{x_k}{\sum_{i=1}^{N}{x_i}}" target="_blank"><img src="https://latex.codecogs.com/gif.latex?\frac{x_k}{\sum_{i=1}^{N}{x_i}}" title="\frac{x_k}{\sum_{i=1}^{N}{x_i}}" /></a>.

Example output from `FTS-TREE`

> Creating Merkle tree with 15 nodes. Hash 1:
> 04CA42FBD140569E6B647B13DDAC3D85B9626FD0B2D98AE6717EBA5B96EB0CCE 
> Hash 2: 0FF2AA9DF6C8845E89A8952A4EC5C74E64FFA39E2813C3D22EB9D31DDE1DE39A
> Hash 3: 7333612E8DD6A02C9B0595E27B537851FCB27B44FF25FFBC024BF554C3E9B939 
> Hash 4: AE0668DDE2F58A9E913480D0B5D3F4F15C4A4945068DD524D9D4B9BFB6480F3E
> Hash 5: 2F4DE43A312F30EA4303F1088E6F4D6EBEB4FFFECF669286423AD2C4D2A3AC18 
> Hash 6: 3EF21DAF7CB37B8E68929872C4132E9021812CC3DE60A5C4345541728CECCE50
> Hash 7: 44E8B580F897CCAA525E7CA6E05D07350F53681FB4D6B4223032C0D6FBFE9537 
> Hash 8: 3AF7519C8A2EDE05A8F5E63041824B453EF989ED616FD80F99A7043DA4303E24
> Hash 9: 67BEBFBB2B7A3312E1C330A5E904FE6B24868D30CB1E78DA16C13C25FE0F7E64 
> Hash 10: 33C51A085F5A65ADB2A6096D8140CBF8E9AFA635D384C2C393A66C262112835B
> Hash 11: 36195B3A1FE4BFAD55817C40C2041B0EF98005BAE6C79F7DE21BF190801A2A56 
> Hash 12: 4F171E075FB62B62EBDA820D3FFCA8287922FEE53710D8FEACD42DEAD202C29B
> Hash 13: D0D8072D186E08048C23B3E92C80F1AB50DD61F8A7E2E8910B64E757A604B604 
> Hash 14: 35F9843C6BF03C26052F4589CF2F11F4A709A02CBC4035E2941E34A2E5B5BA16
> Hash 15: BC1D39F92E64DB7005809B17E2A79FC181BC929968FEAE7B6393420768AFB419 
> Doing follow-the-satoshi in the stake tree 
> Left subtree 51 coins / right subtree 15 coins. Picking coin number 63 
> Choosing right subtree...
> Left subtree 12 coins / right subtree 3 coins. Picking coin number 4
> Choosing left subtree... 
> Left subtree 8 coins / right subtree 4 coins. Picking coin number 1 
> Choosing left subtree... 
> merkleProof {   
>   (0FF2AA9DF6C8845E89A8952A4EC5C74E64FFA39E2813C3D22EB9D31DDE1DE39A, 51, 15)   
>   (44E8B580F897CCAA525E7CA6E05D07350F53681FB4D6B4223032C0D6FBFE9537, 12, 3)   
>   (D0D8072D186E08048C23B3E92C80F1AB50DD61F8A7E2E8910B64E757A604B604, 8, 4) 
> } 
> stakeholder {   
>   Stakeholder 4 
> } 
> Verifying the result 
> Checking audit path... 1 0 0 OK! 
> Next hash: 3EF21DAF7CB37B8E68929872C4132E9021812CC3DE60A5C4345541728CECCE50 
> Next hash: 7333612E8DD6A02C9B0595E27B537851FCB27B44FF25FFBC024BF554C3E9B939
> Next hash: 04CA42FBD140569E6B647B13DDAC3D85B9626FD0B2D98AE6717EBA5B96EB0CCE 
> Root hash matches!

----------

[1] Bentov, Iddo, et al. "Proof of Activity: Extending Bitcoin's Proof of Work via Proof of Stake [Extended Abstract] y." ACM SIGMETRICS Performance Evaluation Review 42.3 (2014): 34-37.
