package team9.LoadBalancerThread.service;

import org.springframework.stereotype.Service;
import team9.LoadBalancerThread.domain.Reservation;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class LbService {

    public static final int TRAFFIC_COUNT = 300000;
    public static final int BUFFER_SIZE = TRAFFIC_COUNT/10;
    public static final int PRODUCER_COUNT = 1;
    public static final int CONSUMER_COUNT = 4;

    Reservation[] buffer;
    int fillIndex;
    int useIndex;
    int countInBuffer;
    public static Lock countInBufferLock;
    Condition producer, consumer;

    public static int consumedCount = 0; //프로듀서가 생성한 개수 main의 state variable
    public static Condition mainCondition; //데이터를 다 넣으면 main을 깨워주기 위한

    public LbService(){
        buffer = new Reservation[BUFFER_SIZE];
        fillIndex = 0;
        useIndex = 0;
        countInBuffer = 0;

        countInBufferLock = new ReentrantLock();
        producer = countInBufferLock.newCondition();
        consumer = countInBufferLock.newCondition();
        mainCondition = countInBufferLock.newCondition();
    }

    public void put(Reservation reservation) {
        buffer[fillIndex] = reservation;
        fillIndex = (fillIndex + 1) % BUFFER_SIZE ;
        countInBuffer++;
    }

    public Reservation get() {
        Reservation tmp = buffer[useIndex];
        useIndex = (useIndex + 1) % BUFFER_SIZE ;
        countInBuffer--;
        consumedCount++;
        return tmp;
    }

    public void start()  throws Exception {

        for(int i=0; i<PRODUCER_COUNT; i++) {
            Thread producer = new Producer();
            producer.start();

        }
        for(int i=0; i<CONSUMER_COUNT; i++) {
            Thread consumer = new Consumer();
            consumer.start();
        }
        countInBufferLock.lock();
        while(consumedCount != TRAFFIC_COUNT)
            mainCondition.await();
        countInBufferLock.unlock();
    }

    public static Reservation genReservation(int id){
        StringBuilder dateOfBirth = new StringBuilder();
        dateOfBirth.append(9);
        for(int i=0; i<5; i++) dateOfBirth.append((int)(Math.random()*9));

        StringBuilder phoneNumber = new StringBuilder();
        phoneNumber.append(0).append(1).append(0);
        phoneNumber.append('-');
        for(int i=0; i<4; i++) phoneNumber.append((int)(Math.random()*9));
        phoneNumber.append('-');
        for(int i=0; i<4; i++) phoneNumber.append((int)(Math.random()*9));

        StringBuilder seatNumber = new StringBuilder();
        seatNumber.append((int)(Math.random()*20));
        return new Reservation(Integer.toString(id), dateOfBirth.toString(),
                phoneNumber.toString(), seatNumber.toString());
    }

    class Producer extends Thread{

        @Override
        public void run(){
            // TODO Auto-generated method stub
            for(int i=1; i<=TRAFFIC_COUNT; i++) {
                countInBufferLock.lock();
                while(countInBuffer == BUFFER_SIZE) {
                    try {
                        producer.await();
                    } catch (Exception e) {e.printStackTrace();}
                }
//                System.out.println("produced");
                put(genReservation(i));
                consumer.signal();
                countInBufferLock.unlock();
            }

        }

    }
    class Consumer extends Thread{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            for(int i=1; i<=TRAFFIC_COUNT/CONSUMER_COUNT; i++) {
                countInBufferLock.lock();
                while(countInBuffer == 0) {
                    try {
                        consumer.await();
                    } catch (Exception e) {e.printStackTrace();}
                }
                // ------------------------
                Reservation reservation = get();
                System.out.println(reservation);
                // 여기서 reservation 객체만 db에 저장해주시면 됩니다.
                // ------------------------
                producer.signal();
                if(consumedCount == TRAFFIC_COUNT) mainCondition.signal();
                countInBufferLock.unlock();
            }
        }

    }

}
