package org.conqueror.bird.gate.scan.file;

import org.apache.commons.lang.StringUtils;
import org.conqueror.bird.exceptions.parse.ParseException;
import org.conqueror.common.utils.date.DateTimeUtils;
import org.conqueror.common.utils.date.DateTimeUtils.TimeUnit;
import org.conqueror.common.utils.file.FileInfo;

import java.text.SimpleDateFormat;
import java.util.*;


public abstract class FileInfoBuilder {

    private static final String TODAY_TAG = "@today";
    private static final String BEFORE_TAG = "@before";
    private static final String AFTER_TAG = "@after";

    private static final String REGEXP_TAG = "@regexp";
    private static final String RANGE_HOUR = "@range_hour";
    private static final String RANGE_DATE = "@range_date";
    private static final String RANGE_MONTH = "@range_month";
    private static final String RANGE_YEAR = "@range_year";
    private static final String RANGE_NUMBER = "@range_number";

    private static final String STAG = "{";
    private static final String RTAG = "~";
    private static final String DTAG = ":";
    private static final String ETAG = "}";

    public abstract List<? extends FileInfo> build(String fileUris, String delimiter) throws ParseException;

    protected abstract String[] getFilePathsInDirectory(String dirPath, String fileRegexp) throws ParseException;

    public List<String> toFileUris(String fileUri, String delimiter) throws ParseException {
        if (delimiter != null) {
            List<String> fileUris = new ArrayList<>();
            String[] tmpFileUris = fileUri.split(delimiter);
            for (String tmpFileUri : tmpFileUris) {
                fileUris.addAll(toFileUris(tmpFileUri));
            }
            return fileUris;
        } else {
            return toFileUris(fileUri);
        }
    }

    public List<String> toFileUris(String fileUri) throws ParseException {
        List<String> fileUris = new ArrayList<>();

        try {
            applyHourRange(fileUri, fileUris);
            List<String> tmpFileUris = fileUris;
            fileUris = new ArrayList<>();

            for (String tmpFileUri : tmpFileUris) applyDayRange(tmpFileUri, fileUris);
            tmpFileUris = fileUris;
            fileUris = new ArrayList<>();

            for (String tmpFileUri : tmpFileUris) applyMonthRange(tmpFileUri, fileUris);
            tmpFileUris = fileUris;
            fileUris = new ArrayList<>();

            for (String tmpFileUri : tmpFileUris) applyYearRange(tmpFileUri, fileUris);
            tmpFileUris = fileUris;
            fileUris = new ArrayList<>();

            for (String tmpFileUri : tmpFileUris) applyNumRange(tmpFileUri, fileUris);
            tmpFileUris = fileUris;
            fileUris = new ArrayList<>();

            for (String tmpFileUri : tmpFileUris) applyBefore(tmpFileUri, fileUris);
            tmpFileUris = fileUris;
            fileUris = new ArrayList<>();

            for (String tmpFileUri : tmpFileUris) applyToday(tmpFileUri, fileUris);
            tmpFileUris = fileUris;
            fileUris = new ArrayList<>();

            for (String tmpFileUri : tmpFileUris) applyAfter(tmpFileUri, fileUris);
            tmpFileUris = fileUris;
            fileUris = new ArrayList<>();

            for (String tmpFileUri : tmpFileUris) applyRegexp(tmpFileUri, fileUris);

        } catch (ParseException e) {
            throw new ParseException("failed to parse the file info string - " + fileUri, e.getCause());
        }

        return fileUris;
    }

    private void applyToday(String input, List<String> dests) {
        SimpleDateFormat formatter = new SimpleDateFormat();

        int idx;
        if ((idx = input.indexOf(TODAY_TAG)) != -1) {
            int sidx = input.indexOf(STAG, idx);
            int eidx = input.indexOf(ETAG, sidx);
            String format = input.substring(sidx + 1, eidx);
            formatter.applyPattern(format);
            String date = formatter.format(new Date());

            input = input.substring(0, idx) + date + input.substring(eidx + 1);
            applyToday(input, dests);
        } else {
            dests.add(input);
        }
    }


    private void applyBefore(String input, List<String> dests) {
        applyDiffDays(input, dests, BEFORE_TAG);
    }

    private void applyAfter(String input, List<String> dests) {
        applyDiffDays(input, dests, AFTER_TAG);
    }

    private void applyDiffDays(String input, List<String> dests, String tag) {
        SimpleDateFormat formatter = new SimpleDateFormat();

        int idx;
        if ((idx = input.indexOf(tag)) != -1) {
            int sidx = input.indexOf(STAG, idx);
            int eidx = input.indexOf(ETAG, sidx);
            String[] formats = input.substring(sidx + 1, eidx).split(":");
            int diff = Integer.parseInt(formats[0]);
            formatter.applyPattern(formats[1]);
            String date = formatter.format(DateTimeUtils.plusDay(new Date(), tag.equals(BEFORE_TAG) ? -diff : diff));

            input = input.substring(0, idx) + date + input.substring(eidx + 1);
            applyDiffDays(input, dests, tag);
        } else {
            dests.add(input);
        }
    }

    private void applyHourRange(String input, List<String> dests) throws ParseException {
        applyDateRange(input, dests, RANGE_HOUR, TimeUnit.HOUR);
    }

    private void applyDayRange(String input, List<String> dests) throws ParseException {
        applyDateRange(input, dests, RANGE_DATE, TimeUnit.DAY);
    }

    private void applyMonthRange(String input, List<String> dests) throws ParseException {
        applyDateRange(input, dests, RANGE_MONTH, TimeUnit.MONTH);
    }

    private void applyYearRange(String input, List<String> dests) throws ParseException {
        applyDateRange(input, dests, RANGE_YEAR, TimeUnit.YEAR);
    }

    private void applyDateRange(String input, List<String> dests, String tag, TimeUnit unit) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat();
        int idx, sidx, ridx, didx, eidx;

        // input : @range_date{sdate~edate:format}
        if ((idx = input.indexOf(tag)) != -1) {
            sidx = input.indexOf(STAG, idx);
            ridx = input.indexOf(RTAG, sidx);
            didx = input.indexOf(DTAG, ridx);
            eidx = input.indexOf(ETAG, didx);
            String sdate = input.substring(sidx + 1, ridx);
            String edate = input.substring(ridx + 1, didx);
            String format = input.substring(didx + 1, eidx);
            formatter.applyPattern(format);

            try {
                Date sDate = formatter.parse(sdate);
                Date eDate = formatter.parse(edate);
                for (Date date = sDate; date.compareTo(eDate) <= 0; date = DateTimeUtils.plusDate(date, 1, unit)) {
                    applyDateRange(input.substring(0, idx) + formatter.format(date) + input.substring(eidx + 1), dests, tag, unit);
                }
            } catch (java.text.ParseException e) {
                throw new ParseException(e);
            }

        } else {
            dests.add(input);
        }
    }

    private void applyNumRange(String input, List<String> dests) {
        int idx;

        // input : @range_number{snumber~enumber}
        if ((idx = input.indexOf(RANGE_NUMBER)) != -1) {
            int sidx = input.indexOf(STAG, idx);
            int ridx = input.indexOf(RTAG, sidx);
            int eidx = input.indexOf(ETAG, ridx);
            String snumb = input.substring(sidx + 1, ridx);
            String enumb = input.substring(ridx + 1, eidx);

            int sNumb = Integer.parseInt(snumb);
            int eNumb = Integer.parseInt(enumb);
            int length = snumb.length();
            for (int numb = sNumb; numb <= eNumb; numb++) {
                applyNumRange(input.substring(0, idx) + StringUtils.leftPad(Integer.toString(numb), length, "0") + input.substring(eidx + 1), dests);
            }
        } else {
            dests.add(input);
        }
    }

    private void applyRegexp(String input, List<String> dests) throws ParseException {
        int idx, sidx, eidx, dirEidx;
        String pattern = "", dirPath;

        // input : @regexp{expression}
        if ((idx = input.indexOf(REGEXP_TAG)) != -1) {
            sidx = input.indexOf(STAG, idx);
            eidx = input.indexOf(ETAG, sidx);
            dirEidx = input.lastIndexOf("/");
            if (dirEidx + 1 < idx) pattern += input.substring(dirEidx + 1, idx);
            if (sidx < eidx) pattern += input.substring(sidx + 1, eidx);
            if (eidx <= input.length()) pattern += input.substring(eidx + 1);

            dirPath = input.substring(0, dirEidx);

            String[] list = getFilePathsInDirectory(dirPath, pattern);
            Collections.addAll(dests, list);
        } else {
            dests.add(input);
        }
    }

}
