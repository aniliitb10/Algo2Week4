import edu.princeton.cs.algs4.SET;

public class BoggleSolver
{
   private static final int prefixCutOffDepth = 6;
  private static final int scoreDepth = 3;
  private static final char QChar = 'Q';
  private static final String QStr = "QU";

  private final ModifiedTrieSET trieSET;
  private final int[] scores;
  private final int maxWordLength;


  // Initializes the data structure using the given array of strings as the dictionary.
  // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
  public BoggleSolver(String[] dictionary)
  {
    trieSET = new ModifiedTrieSET();

    // initializing scores
    scores = new int[8];
    scores[0] = 0;
    scores[1] = 0;
    scores[2] = 0;
    scores[3] = 1;
    scores[4] = 1;
    scores[5] = 2;
    scores[6] = 3;
    scores[7] = 5;

    int maxLength = 0;
    for (String word : dictionary)
    {
      int length = word.length();
      if (length > maxLength)  maxLength = length;

      trieSET.add(word);
    }

    this.maxWordLength = maxLength;
  }

  // Returns the set of all valid words in the given Boggle board, as an Iterable.
  public Iterable<String> getAllValidWords(BoggleBoard board)
  {
    boolean[][] onStack = new boolean[board.rows()][board.cols()];
    SET<String> words = new SET<>();

    for (int rowIndex = 0; rowIndex < board.rows(); ++rowIndex)
    {
      for (int colIndex = 0; colIndex < board.cols(); ++colIndex)
      {
        onStack[rowIndex][colIndex] = true;

        int depth = 0;

        StringBuilder prefix = new StringBuilder();
        char c = board.getLetter(rowIndex, colIndex);
        if (c == QChar)
        {
          prefix.append(QStr);
          depth += 2;
        }
        else
        {
          prefix.append(c);
          depth += 1;
        }

        searchWord(board, rowIndex, colIndex, onStack, words, prefix, depth, null);
        onStack[rowIndex][colIndex] = false;
      }
    }

    return words;
  }

  // Returns the score of the given word if it is in the dictionary, zero otherwise.
  // (You can assume the word contains only the uppercase letters A through Z.)
  public int scoreOf(String word)
  {
    if (!trieSET.contains(word)) return 0;
    return scoreOf(word.length());
  }

  private int scoreOf (int wordLength)
  {
    if (wordLength < scores.length) return scores[wordLength];
    return 11;
  }

  // The letter at (rowIndex, colIndex) is already appended in prefix
  // and onStack is true for the same
  // this call is to handle the neighbors of point at (rowIndex, colIndex)
  // depth should be equal to the length of prefix
  private void searchWord(BoggleBoard board, int rowIndex, int colIndex, boolean[][] onStack, SET<String> words,
                          StringBuilder prefix, int depth, ModifiedTrieSET withPrefix)
  {
    if (depth > maxWordLength) return;
    if (depth >= prefixCutOffDepth)
    {
      withPrefix = new ModifiedTrieSET();
      for (String keyWithPrefix : trieSET.keysWithPrefix(prefix.toString()))
      {
        withPrefix.add(keyWithPrefix);
      }
      if (withPrefix.isEmpty()) return;
    }

    if (colIndex < (board.cols() - 1)) // we can go right
    {
      searchWordImpl(board, rowIndex, colIndex+1, onStack, words, prefix, depth, withPrefix);

      if (rowIndex < (board.rows() - 1)) // we can go right down
      {
        searchWordImpl(board, rowIndex+1, colIndex+1, onStack, words, prefix, depth, withPrefix);
      }

      if (rowIndex > 0) // we can go right up
      {
        searchWordImpl(board, rowIndex-1, colIndex+1, onStack, words, prefix, depth, withPrefix);
      }
    }

    if (colIndex > 0) // we can go left
    {
      searchWordImpl(board, rowIndex, colIndex-1, onStack, words, prefix, depth, withPrefix);

      if (rowIndex < (board.rows() - 1)) // we can go left down
      {
        searchWordImpl(board, rowIndex+1, colIndex-1, onStack, words, prefix, depth, withPrefix);
      }

      if (rowIndex > 0) // we can go left up
      {
        searchWordImpl(board, rowIndex-1, colIndex-1, onStack, words, prefix, depth, withPrefix);
      }
    }

    if (rowIndex > 0) // we can go up
    {
      searchWordImpl(board, rowIndex-1, colIndex, onStack, words, prefix, depth, withPrefix);
    }

    if (rowIndex < (board.rows()-1)) // we can go down
    {
      searchWordImpl(board, rowIndex+1, colIndex, onStack, words, prefix, depth, withPrefix);
    }
  }

  // Complements searchWord
  // searchWord finds the neighbor (and hence updates rowIndex, colIndex)
  // then calls searchWordImpl
  private void searchWordImpl(BoggleBoard board, int rowIndex, int colIndex, boolean[][] onStack, SET<String> words,
                              StringBuilder prefix, int depth, ModifiedTrieSET withPrefix)
  {
    if (onStack[rowIndex][colIndex]) return;

    // this needs to be reverted in the end
    onStack[rowIndex][colIndex] = true;
    char c = board.getLetter(rowIndex, colIndex);
    if (c == QChar)
    {
      prefix.append(QStr);
      depth += 2;
    }
    else
    {
      prefix.append(c);
      depth += 1;
    }

    // actual operation
    if (depth >= scoreDepth)
    {
      ModifiedTrieSET wordsSource = ((withPrefix != null) ? withPrefix : trieSET);

      String prefixStr = prefix.toString();
      if(wordsSource.contains(prefixStr))
      {
        words.add(prefixStr);
      }
    }

    searchWord(board, rowIndex, colIndex, onStack, words, prefix, depth, withPrefix);

    // clean up
    onStack[rowIndex][colIndex] = false;
    if (c == QChar)
    {
      prefix.delete(depth-2, depth);
    }
    else
    {
      prefix.deleteCharAt(depth-1);
    }
  }
}
