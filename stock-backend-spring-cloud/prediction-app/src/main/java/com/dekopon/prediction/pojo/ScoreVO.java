package com.dekopon.prediction.pojo;

import lombok.Data;

@Data
public class ScoreVO {
    Double open;
    Double high;
    Double low;
    Double close;
    Double volume;
    Integer openNum;
    Integer highNum;
    Integer lowNum;
    Integer closeNum;
    Integer volumeNum;

    public final static ScoreVO INVALID_SCORE_VO;
    static {
        ScoreVO scoreVO = new ScoreVO();
        scoreVO.open = Double.NaN;
        scoreVO.high = Double.NaN;
        scoreVO.low = Double.NaN;
        scoreVO.close = Double.NaN;
        scoreVO.volume = Double.NaN;
        INVALID_SCORE_VO = scoreVO;
    }
}
