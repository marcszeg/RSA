import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class Rsa
{
    public static BigInteger[] generateKey()
    {
        BigInteger[] key = new BigInteger[3];

        BigInteger p, q;
        p = generatePrime();
        //System.out.println("p:  " + p);
        q = generatePrime();
        while(p.equals(q))
        {
            q = generatePrime();
        }
        //System.out.println("q:  " + q);

        BigInteger n = p.multiply(q);
        key[0] = n;

        BigInteger fi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        //System.out.println("fi: " + fi);

        BigInteger e = null;
        int a;
        BigInteger b;
        for(a=3; BigInteger.valueOf(a).compareTo(fi) == -1; a++)
        {
            BigInteger[] v1 = eea(BigInteger.valueOf(a), fi);
            b = v1[1];
            if (b.compareTo(BigInteger.ONE) == 0)
            {
                e = BigInteger.valueOf(a);
                break;
            }
        }
        key[1] = e;

        BigInteger d;
        BigInteger[] v2 = eea(e, fi);
        BigInteger c;
        if (v2[0].mod(BigInteger.TWO).compareTo(BigInteger.ZERO) == 0)
            c = BigInteger.ONE;
        else
            c = BigInteger.valueOf(-1);
        BigInteger x = c.multiply(v2[3]);
        if(BigInteger.ONE.compareTo(x) < 0 && x.compareTo(fi) < 0)
            d = x;
        else
            d = x.add(fi);
        key[2] = d;

        return key;
    }

    public static BigInteger generatePrime()
    {
        boolean notgood = true;
        BigInteger[] base = new BigInteger[3];
        BigInteger prime = null;
        int a = 0;

        while(notgood)
        {
            Random r = new Random();
            BigInteger test = new BigInteger(20, r);
            if(test.mod(BigInteger.TWO).equals(BigInteger.ZERO))
                continue;

            base = generateBase();

            for (int i = 0; i<3; i++)
            {
                if( !mr(test, base[i]) )
                {
                    a++;
                }
                else
                    break;
            }
            if (a == 3)
            {
                prime = test;
                break;
            }
            a=0;
        }

        return prime;
    }

    public static BigInteger[] generateBase()
    {
        BigInteger[] base = new BigInteger[3];
        int i = 0;
        boolean notdone = true;
        int random;
        BigInteger a;

        while (notdone)
        {
            random = new Random().nextInt(100);
            a = BigInteger.valueOf(random);

            if (i == 0)
            {
                base[i] = a;
                i++;
                continue;
            }
            else if (i == 1)
            {
                if (base[0].equals(a))
                    continue;
                else
                {
                    base[i] = a;
                    i++;
                    continue;
                }
            }
            else if (i == 2)
            {
                if (base[0].equals(a) || base[1].equals(a))
                    continue;
                else
                {
                    base[i] = a;
                    notdone = false;
                }
            }

        }

        return base;
    }

    public static boolean mr(BigInteger n, BigInteger base)
    {
        BigInteger d = n.subtract(BigInteger.ONE);

        int s = 0;
        while(d.mod(BigInteger.TWO).equals(BigInteger.ZERO))
        {
            s++;
            d = d.divide(BigInteger.TWO);
        }

        if(base.modPow(d, n).equals(BigInteger.ONE))    //base^d mod n =? 1
        {
            return false;
        }

        for(int r=0; r<s; r++)      //base^((2^r)*d) mod n ?= -1,  r E {0,...,s-1}
        {
            if(base.modPow((BigInteger.TWO.pow(r)).multiply(d), n).equals(BigInteger.valueOf(-1)))
            {
                return false;
            }
        }

        return true;
    }

    public static BigInteger[] eea(BigInteger a, BigInteger b)
    {
        BigInteger[] v = new BigInteger[5];

        BigInteger[][] table = new BigInteger[4][1000];
        table[0][0] = a;
        table[0][1] = b;
        table[0][2] = a.mod(b);
        table[2][0] = BigInteger.ONE;
        table[2][1] = BigInteger.ZERO;
        table[3][0] = BigInteger.ZERO;
        table[3][1] = BigInteger.ONE;

        int i = 2;

        for (;;)
        {
            table[0][i] = table[0][i-2].mod(table[0][i-1]);
            table[1][i-1] = table[0][i-2].divide(table[0][i-1]);
            table[2][i] = table[2][i-1].multiply(table[1][i-1]).add(table[2][i-2]);
            table[3][i] = table[3][i-1].multiply(table[1][i-1]).add(table[3][i-2]);

            if(table[0][i].equals(BigInteger.ZERO))
                break;

            i++;
        }

        v[0] = BigInteger.valueOf(i-1);  //N
        v[1] = table[0][i-1];            //rN
        v[2] = table[1][i-1];;           //qN
        v[3] = table[2][i-1];;           //xN
        v[4] = table[3][i-1];;           //yN

        return v;
    }

    public static BigInteger fme(BigInteger base, BigInteger e, BigInteger m)
    {
        String binpow = e.toString(2);
        BigInteger[] powmods = new BigInteger[binpow.length()];
        powmods[binpow.length()-1] = base.mod(m);
        int i = binpow.length()-2;

        while(i >= 0)
        {
            powmods[i] = powmods[i+1].pow(2).mod(m);
            i--;
        }

        BigInteger c = BigInteger.ONE;

        for(int j=0; j<binpow.length(); j++)
        {
            if(binpow.charAt(j) == '1')
                c = c.multiply(powmods[j]);
        }

        return c.mod(m);
    }

    public static void main(String[] args)
    {
        BigInteger[] key = generateKey();        //key[] = {n, e, d}
        BigInteger n = key[0];
        /*System.out.println("n:  " + n);
        BigInteger f = key[1];
        System.out.println("e:  " + f);
        BigInteger d = key[2];
        System.out.println("d:  " + d); */
        System.out.println("Addja meg a titkosítani/visszafejteni kívánt üzenetet:");
        Scanner myBigInt = new Scanner(System.in);
        BigInteger m = myBigInt.nextBigInteger();
        while(m.compareTo(n) != -1)
        {
            System.out.println("A megadott üzenet túl nagy! Addjon meg egy kisebb üzenetet:");
            m = myBigInt.nextBigInteger();
        }

        System.out.println("Addja meg, hogy titkosítani (e) vagy visszafejteni (d) szeretné a megadott üzenetet:");
        Scanner in = new Scanner(System.in);
        String answer = in.next();

        if (answer.equals("e"))
        {
            System.out.println("A titkosítást választotta.");
            BigInteger encryptedm = fme(m, key[1], key[0]);         //m e n
            System.out.println("A titkosított üzenet: " + encryptedm);

            BigInteger checkm = fme(encryptedm, key[2], key[0]);    //enc(m) d n
            System.out.println("Az eredeti üzenet: " + checkm);
            if (checkm.compareTo(m) != 0)
                System.out.println("Valmai hiba történt...");
        }
        else
        {
            System.out.println("A visszafejtést választotta.");
            BigInteger decrypredc = fme(m, key[2], key[0]);             //c d n
            System.out.println("A visszafejtett üzenet: " + decrypredc);

            BigInteger checkc = fme(decrypredc,  key[1], key[0]);       //dec(c) e n
            System.out.println("Az eredeti titkosított üzenet: " + checkc);
            if (checkc.compareTo(m) != 0)
                System.out.println("Valmai hiba történt...");
        }
    }
}
