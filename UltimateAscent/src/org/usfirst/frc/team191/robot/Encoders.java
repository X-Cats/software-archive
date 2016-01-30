/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

/**
 *
 * @author mary farmer
 */
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Timer;

public class Encoders {

    private Encoder m_rrEncoder;
    private Encoder m_rfEncoder;
    private Encoder m_lrEncoder;
    private Encoder m_lfEncoder;
    private Timer m_rrTimer = new Timer();
    private Timer m_rfTimer = new Timer();
    private Timer m_lrTimer = new Timer();
    private Timer m_lfTimer = new Timer();
    private int m_distanceAccumulator = 0;

    Encoders() {

        if (m_rrEncoder == null) {
            m_rrEncoder = new Encoder(Enums.RIGHT_REAR_ENCODER_A, Enums.RIGHT_REAR_ENCODER_B, false, EncodingType.k4X);
        }
        if (m_rfEncoder == null) {
            m_rfEncoder = new Encoder(Enums.RIGHT_FRONT_ENCODER_A, Enums.RIGHT_FRONT_ENCODER_B, false, EncodingType.k4X);
        }
        if (m_lrEncoder == null) {
            m_lrEncoder = new Encoder(Enums.LEFT_REAR_ENCODER_A, Enums.LEFT_REAR_ENCODER_B, true, EncodingType.k4X);
        }
        if (m_lfEncoder == null) {
            m_lfEncoder = new Encoder(Enums.LEFT_FRONT_ENCODER_A, Enums.LEFT_FRONT_ENCODER_B, true, EncodingType.k4X);
        }

        /*
        m_lrEncoder.setDistancePerPulse(0.1);
        m_lfEncoder.setDistancePerPulse(0.1);
        m_rrEncoder.setDistancePerPulse(0.1);
        m_rfEncoder.setDistancePerPulse(0.1);
         */

        reset();
    }

    public final void ronBarDistance() {
//        printRaw();
//        System.out.println("Acc: "+m_distanceAccumulator);
        double distance = 0;
        boolean bFound = false;
        int count=0;
        if (m_lfEncoder.getRaw() > 0) {
            count ++;
            distance += m_lfEncoder.getRaw();
            bFound = true;
        }
        if (m_rfEncoder.getRaw() > 0) {
            count ++;
            distance += m_rfEncoder.getRaw();
            bFound = true;
        }
        if (m_lrEncoder.getRaw() > 0) {
            count ++;
            distance += m_lrEncoder.getRaw();
            bFound = true;
        }
        if (m_rrEncoder.getRaw() > 0) {
            count ++;
            distance += m_rrEncoder.getRaw();
            bFound = true;
        }

        //m_distanceAccumulator += m_lfEncoder.getRaw();
        if (bFound) {
            m_distanceAccumulator += (distance/count);
        }


    }

    public final void reset() {
        ronBarDistance();

        m_lrEncoder.reset();
        m_rrEncoder.reset();
        m_lfEncoder.reset();
        m_rfEncoder.reset();

        m_lfEncoder.start();
        m_rfEncoder.start();
        m_lrEncoder.start();
        m_rrEncoder.start();

        m_lfTimer.reset();
        m_rfTimer.reset();
        m_rrTimer.reset();
        m_lrTimer.reset();

        m_lfTimer.start();
        m_rfTimer.start();
        m_rrTimer.start();
        m_lrTimer.start();
    }

    public int getAccumulator() {
        return m_distanceAccumulator;
    }

    public void resetAccumulator() {
        System.out.println("ResetAccumulator");
        m_distanceAccumulator = 0;
    }

    public double getDistance() {
        double d = lowestEncoder();
        return d;
    }

    private double lowestEncoder() {
        double retVal = 0;

        System.out.println("LF: " + m_lfEncoder.getDistance() + ", RF: " + m_rfEncoder.getDistance());
        if (Math.min(m_lfEncoder.getDistance(), m_rfEncoder.getDistance()) == 0) {
            retVal = Math.max(m_lfEncoder.getDistance(), m_rfEncoder.getDistance());
        } else {
            retVal = Math.min(m_lfEncoder.getDistance(), m_rfEncoder.getDistance());
        }
        return retVal;
    }

    public void printPeriod() {
        System.out.println("RR: " + m_rrEncoder.getPeriod());
    }

    public void printRaw() {
        System.out.println("LF: " + m_lfEncoder.getRaw() + ", RF: " + m_rfEncoder.getRaw()+", LR: " + m_lrEncoder.getRaw() + ", RR: " + m_rrEncoder.getRaw());
//        System.out.println("RF: " + m_rfEncoder.getRaw());
    }

    public void printDistance() {
        System.out.println("LF: " + m_lfEncoder.getDistance() + ", RF: " + m_rfEncoder.getDistance() + ", LR: " + m_lrEncoder.getDistance() + ", RR: " + m_rrEncoder.getDistance());

    }

    public void printCount() {
        System.out.println("RR: " + m_rrEncoder.get());
    }

    public void printRate() {
        //note of the 2011 robot, the LR and RR encoders keep reading NaN, even though they do getRaw ok
        System.out.println("LF: " + m_lfEncoder.getRate() + ", RF: " + m_rfEncoder.getRate() + ", LR: " + m_lrEncoder.getRate() + ", RR: " + m_rrEncoder.getRate());
    }

    public void printCalcRates() {
        System.out.println("LF: " + getLFrate() + ", RF: " + getRFrate() + ", LR: " + getLRrate() + ", RR: " + getRRrate());
    }

    public double getRRrate() {
        try {
            return m_rrEncoder.getRaw() / m_rrTimer.get();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public double getRFrate() {
        try {
            return m_rfEncoder.getRaw() / m_rfTimer.get();

        } catch (Exception ex) {
            return 0.0;
        }
    }

    public double getLRrate() {
        try {
            return m_lrEncoder.getRaw() / m_lrTimer.get();

        } catch (Exception ex) {
            return 0.0;
        }
    }

    public double getLFrate() {
        try {
            return m_lfEncoder.getRaw() / m_lfTimer.get();

        } catch (Exception ex) {
            return 0.0;
        }
    }
}
