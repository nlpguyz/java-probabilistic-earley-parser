package org.leibnizcenter.cfg.earleyparser.parse;

import org.leibnizcenter.cfg.token.TokenWithCategories;

/**
 * Functional interface to intervene on scan probabilities.
 * <p>
 * Created by Maarten on 31-7-2016.
 */
@FunctionalInterface
public interface ScanProbability<T> {
    double getProbability(int index, TokenWithCategories<T> tokenWithCategories);
}
