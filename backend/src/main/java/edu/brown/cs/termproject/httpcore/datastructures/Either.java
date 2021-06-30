package edu.brown.cs.termproject.httpcore.datastructures;

/**
 * A (STRIPPED DOWN) version of Either (not a real monoid or whatever).
 */
public class Either<L, R> {

  private final L left;
  private final R right;

  private Either(L l, R r) {
    this.left = l;
    this.right = r;
  }

  public static <Lp, Rp> Either<Lp, Rp> left(Lp leftPrime) {
    return new Either<Lp, Rp>(leftPrime, null);
  }

  public static <Lp, Rp> Either<Lp, Rp> right(Rp rightPrime) {
    return new Either<Lp, Rp>(null, rightPrime);
  }

  public boolean isLeft() {
    return left != null;
  }
  public boolean isRight() {
    return right != null;
  }

  public L getLeft() {
    return left;
  }

  public R getRight() {
    return right;
  }
}
