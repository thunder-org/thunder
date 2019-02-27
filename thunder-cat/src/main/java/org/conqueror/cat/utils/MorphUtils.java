package org.conqueror.cat.utils;

import da.klay.common.pos.Pos;
import da.klay.core.morphology.analysis.Morph;
import da.klay.core.morphology.analysis.Morphs;
import org.apache.commons.lang3.CharSequenceUtils;
import org.conqueror.common.utils.string.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.conqueror.common.utils.string.StringUtils.*;


public class MorphUtils {

    /* 체언 */
    public static final String NNG = "NNG";     // 일반 명사
    public static final String NNP = "NNP";     // 고유 명사
    public static final String NNB = "NNB";     // 의존 명사
    public static final String NR = "NR";       // 수사
    public static final String NP = "NP";       // 대명사

    /* 용언 */
    public static final String VV = "VV";       // 동사
    public static final String VA = "VA";       // 형용사
    public static final String VX = "VX";       // 보조 용언
    public static final String VCP = "VCP";     // 긍정 지정사, 서술격 조사 '이다'
    public static final String VCN = "VCN";     // 부정 지정사, 형용사 '아니다'

    /* 관형사 */
    public static final String MM = "MM";       // 관형사

    /* 부사 */
    public static final String MAG = "MAG";     // 일반 부사
    public static final String MAJ = "MAJ";     // 접속 부사

    /* 감탄사 */
    public static final String IC = "IC";       // 감탄사

    /* 조사 */
    public static final String JKS = "JKS";     // 주격 조사
    public static final String JKC = "JKC";     // 보격 조사
    public static final String JKG = "JKG";     // 관형격 조사
    public static final String JKO = "JKO";     // 목적격 조사
    public static final String JKB = "JKB";     // 부사격 조사
    public static final String JKV = "JKV";     // 호격 조사
    public static final String JKQ = "JKQ";     // 인용격 조사
    public static final String JX = "JX";       // 보조사
    public static final String JC = "JC";       // 접속 조사

    /* 어미 */
    public static final String EP = "EP";       // 선어말 어미
    public static final String EF = "EF";       // 종결 어미
    public static final String EC = "EC";       // 연결 어미
    public static final String ETN = "ETN";     // 명사형 전성 어미
    public static final String ETM = "ETM";     // 관형형 전성 어미

    /* 접두사 */
    public static final String XPN = "XPN";     // 체언 접두사

    /* 접미사 */
    public static final String XSN = "SXN";     // 명사 파생 접미사
    public static final String XSV = "XSV";     // 동사 파생 접미사
    public static final String XSA = "XSA";     // 형용사 파생 접미사
    public static final String XsV = "XsV";     //

    /* 어근 */
    public static final String XR = "XR";       // 어근

    /* 부호 */
    public static final String SF = "SF";       // 마침표, 물음표, 느낌표
    public static final String SP = "SP";       // 쉼표, 가운뎃점, 콜론, 빗금
    public static final String SS = "SS";       // 따옴표, 괄호표, 줄표
    public static final String SE = "SE";       // 줄임표
    public static final String SO = "SO";       // 붙임표(물결, 숨김, 빠짐)
    public static final String SW = "SW";       // 기타기호(논리수학기호, 화폐기호)

    /* 한글이외 */
    public static final String SL = "SL";       // 외국어
    public static final String SH = "SH";       // 한자
    public static final String SN = "SN";       // 숫자
    public static final String NA = "NA";       // 기타

    public static final String PERIOD = ".";
    public static final String COMMA = ",";

    public static class MorphRange {

        private int bom;
        private int eom;

        public MorphRange(int bom, int eom) {
            this.bom = bom;
            this.eom = eom;
        }

        public int getBom() {
            return bom;
        }

        public int getEom() {
            return eom;
        }

        public static boolean hasMorphRange(int bom, int eom) {
            return bom != -1 && eom != -1;
        }

    }

    public static final char[] APOSTROPHE1 = new char[]{'\''};
    public static final char[] APOSTROPHE2 = new char[]{'`'};

    public static boolean equalsIn(CharSequence pos, CharSequence... compPoses) {
        return equalsIn(pos, (Iterable<? extends CharSequence>) Arrays.asList(compPoses));
    }

    public static boolean equalsIn(CharSequence pos, List<? extends CharSequence> compPoses) {
        return equalsIn(pos, (Iterable<? extends CharSequence>) compPoses);
    }

    public static boolean equalsIn(CharSequence pos, Iterable<? extends CharSequence> compPoses) {
        for (CharSequence compPos : compPoses) {
            if ((pos.equals(compPos))) {
                return true;
            }
        }
        return false;
    }

    // 품사가 문법 형태소인지 체크
    public static boolean isGrammaticalMorph(CharSequence tag) {
        char firstByte = tag.charAt(0);
        char secondByte = tag.charAt(1);

        switch (firstByte) {
            case 'S':
                switch (secondByte) {
                    case 'S':
                    case 'F':
                    case 'P':
                    case 'E':
                    case 'O':
                        return true;
                }
                break;
            case 'I':
                if (secondByte == 'C') return true;
                break;
        }

        return false;
    }

//    public static boolean isPunctuationMark(Morphs morphs, Morph morph) {
//        return isPunctuationMark(morphs, morph.getNumber());
//    }

//    public static boolean isPunctuationMark(Morphs morphs, int mnum) {
//        Morph morph = morphs.getMorph(mnum);
//        Morph fmorph = (mnum > 0) ? morphs.getMorph(mnum - 1) : null;
//        Morph nmorph = morphs.getMorph(mnum + 1);
//        String text = morph.getTextStr();
//
//        if (isSpecialChar(text)) {
//            if (text.matches("([?!,;/\"“”(){}…]|.{2,})")) {
//                return true;
//            } else if (text.matches("[-:/~∼.,'‘’]")) {
//                if (fmorph == null || nmorph == null) return true;
//                else {
//                    if (text.matches("[-]")) {
//                        if (!((isEnglish(fmorph) || isNumber(fmorph)) && (isEnglish(nmorph) || isNumber(nmorph)))) {
//                            return true;
//                        }
//                    } else if (text.matches("[:/~∼.,]")) {
//                        if (!(isNumber(fmorph) && isNumber(nmorph))) {
//                            return true;
//                        }
//                    } else if (text.matches("['‘’]")) {
//                        char[] nchars = nmorph.getText();
//                        if (!(isEnglish(fmorph) && nchars.length == 1 && nchars[0] == 's')) {
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//
//        return false;
//    }

    // 품사가 sn|etc|emo|njm 인지 체크
//    public static boolean isSymbol(Morph morph) {
//        CharSequence tag = morph.getPos();
//        char firstByte = tag.charAt(0);
//        char secondByte = 0;
//        char thirdByte = 0;
//
//        int posLength = tag.length();
//        if (posLength > 1) secondByte = tag.charAt(1);
//        if (posLength > 2) thirdByte = tag.charAt(2);
//
//        switch (firstByte) {
//            case 's':
//                if (secondByte != 'n') return true;
//                break;
//            case 'e':
//                if ((secondByte == 't' && thirdByte == 'c')
//                    || (secondByte == 'm' && thirdByte == 'o'))
//                    return true;
//                break;
//            case 'n':
//                if (secondByte == 'j' && thirdByte == 'm') return true;
//                break;
//        }
//
//        return false;
//    }

    /*
        한글,영어,기호 조합의 키워드를 각각 분리
        예1) 잘생겼다LTE --> 잘생겼다, LTE
        예2) 삼성+엘지 --> 삼성, 엘지
        예3) 갤럭시S3 --> 갤럭시, S3
        예4) 갤럭시에스3출시 --> 갤럭시에스3, 출시
    */
    public static List<String> splitWords(String word) {
        List<String> segmentsInOneWord = new ArrayList<String>();

        char[] wordChars = word.toCharArray();
        int wordLength = wordChars.length;
        for (int sCharIdx = 0; sCharIdx < wordLength; ) {
            CharCodeType code = getCharCodeType(wordChars[sCharIdx]), nextCode;

            int eCharIdx = sCharIdx + 1;
            if (code != CharCodeType.ETC) {
                while (eCharIdx < wordLength) {
                    nextCode = getCharCodeType(wordChars[eCharIdx]);
                    if (nextCode != CharCodeType.NUM && code != nextCode) break;

                    eCharIdx++;
                }

                // scharIdx ~ echarIdx
                segmentsInOneWord.add(charsToString(wordChars, sCharIdx, eCharIdx));
            }

            sCharIdx = eCharIdx;
        }

        return segmentsInOneWord;
    }

    /**
     * 숫자에 기호가 포함될 경우 숫자로 추출
     * - 소숫점 (10.3 -> 10.3)
     * - 구분자 (10,000 -> 10000)
     *
     * @param morphs morphs
     * @param bom    the number of first morph
     * @param eom    the number of last morph
     * @return the number of last morph, or -1 if the morphs are not a number,
     */
    public static int extractEndMorphOfNumber(Morphs morphs, int bom, int eom) {
        int dotCnt = 0;
        int commaCnt = 0;
        int numberLength = 0;
        int realEom = -1;

        for (int mnum = bom; mnum <= eom; mnum++) {
            Morph morph = morphs.getMorphs().get(mnum);
            CharSequence text = morph.getText();
            if (morph.getPos().equals(SN)) {
                numberLength = text.length();
            } else if (text.equals(PERIOD)) {
                if (++dotCnt > 1) {
                    return -1;
                }
            } else if (text.equals(COMMA)) {
                if (commaCnt > 0) {
                    if (numberLength != 3) {
                        return -1;
                    }
                    commaCnt++;
                } else {
                    commaCnt++;
                }
            } else {
                break;
            }

            realEom = eom;
        }

        return realEom;
    }

    public static String morphsToWord(List<CharSequence> morphs) {
        if (morphs.size() > 1) {
            StringBuilder word = new StringBuilder();
            for (CharSequence morph : morphs) word.append(morph);
            return word.toString();
        }

        return morphs.get(0).toString();
    }

    public static String morphsToWord(Morphs morphs, int bom, int eom) {
        StringBuilder word = new StringBuilder();
        for (int mnum = bom; mnum <= eom; mnum++) {
            word.append(morphs.getMorphs().get(mnum).getText());
        }

        return word.toString();
    }

    /**
     * 숫자에 기호가 포함될 경우 숫자로 추출
     * - 소숫점 (10.3 -> 10.3)
     * - 구분자 (10,000 -> 10000)
     */
    public static String morphsToNumber(Morphs morphs, int bom, int eom) {
        StringBuilder word = new StringBuilder();
        for (int mnum = bom; mnum <= eom; mnum++) {
            Morph morph = morphs.getMorphs().get(mnum);
            if (isNumber(morph)) word.append(morphs.getText());
        }
        return word.toString();
    }

    public static boolean isNumber(Morph morph) {
        return morph.getPos().equals(SN);
    }

    public static boolean isEnglish(Morph morph) {
        return morph.getPos().equals(SL) && StringUtils.isEnglishChar(morph.getText());
    }

//    public static boolean isJapanese(Morph morph) {
//        return morph.equals(MorphConstants.jpn);
//    }

//    public static boolean isChinese(Morph morph) {
//        return morph.equals(MorphConstants.chn);
//    }

    public static boolean isNoun(Morph morph) {
        return equalsIn(morph.getPos(), NNG, NNP);
    }

    public static boolean hasSpace(Morphs morphs, int bom, int eom) {
        int tnum = morphs.getMorphs().get(bom).getTokenNumber();
        for (int mnum = bom + 1; mnum <= eom; mnum++) {
            if (tnum != morphs.getMorphs().get(bom).getTokenNumber()) return false;
        }
        return true;
    }

//    public static boolean isBlankToken(int tokenType) {
//        return tokenType == TokenConstants.CHART_BLNK;
//    }

    public static MorphRange getMorphRangeOfToken(Morphs morphs, int tokenNumber) {
        int bom = -1;
        int eom = -1;
        for (int mnum = 0; mnum < morphs.getMorphs().size(); mnum++) {
            if (tokenNumber == morphs.getMorphs().get(mnum).getTokenNumber()) {
                if (bom == -1) bom = eom = mnum;
                else eom = mnum;
            } else {
                break;
            }
        }
        return (bom != -1) ? new MorphRange(bom, eom) : null;
    }

}
