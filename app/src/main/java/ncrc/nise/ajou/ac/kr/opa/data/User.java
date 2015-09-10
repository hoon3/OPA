package ncrc.nise.ajou.ac.kr.opa.data;

public class User {
    private String name;
    private int sex;
    private int age;
    private double height;
    private double weight;
    //private double waist_length;


    public User(double height, double weight, int age, int sex) {
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() { // 0:female, 1:male
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    /*
    public double getWaist_length() {
        return waist_length;
    }

    public void setWaist_length(double waist_length) {
        this.waist_length = waist_length;
    }
    */

    public double getDailyBMR() {
        //Get basal metabolic rate vy Mifflin, MD; St Jeor, ST equation
        //wieght : kg, height: cm, age: year
        // return kcal/day

        return (10.0 * weight) + (6.25 * height) + (5.0 * age) + (166 * sex - 161);
    }

    public double getBMI() {
        return weight / (height * height / 10000);
    }

    public String getOverweightStatus() {
        double bmi = getBMI();

        if(bmi > 35 ) {
            return "고도비만";
        }
        if(bmi > 30 ) {
            return "중등도비만";
        }
        if(bmi > 25 ) {
            return "경도비만";
        }
        if(bmi > 23 ) {
            return "과체중";
        }
        if(bmi >18.5) {
            return "정상";
        }
        return "저체중";
    }
}
