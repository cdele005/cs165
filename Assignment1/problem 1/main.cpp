#include <iostream>
#include <fstream>
#include <vector>

using namespace std;

//                         a    b    c    d    e    f    g    h    i    j
vector<char> key         {'v', 'n', 'o', 's', 'w', 'k', '.', 't', 'f', 'p',

//                         k    l    m    n    o    p    q    r    s    t 
                          'u', 'l', ' ', 'a', 'r', '!', 'j', 'r', 's', 'd',

//                         u    v    w    x    y    z    ,    .    !   ' '
                          'b', 'h', 'x', 'i', 'g', ',', 'm', 'c', 'e', 'y'};

struct Freq
{
    char c;
    float f;
};

// print frequencies (specific to freq_vec)
void print_freq(vector<Freq> &f)
{
    for (unsigned i = 0; i < f.size(); i++)
    {
        cout << "'" <<  f.at(i).c << "'  = " << f.at(i).f; 

        if (i < f.size() - 1)
        {cout << endl;}
    }
}

// counts the frequency of each letter in c and stores in f
void count(vector<Freq> &f, vector<char> c)
{
    for (unsigned i = 0; i < c.size(); i++)
    {
        char a = c.at(i);
        for (int j = 0; j < 26; j++)
        {
            if (a == 'a' + j || a == 'A' + j)
            {
                f.at(j).f++;
            }
        }

        if (a == ',')
        {f.at(26).f++;}

        else if (a == '.')
        {f.at(27).f++;}

        else if (a == '!')
        {f.at(28).f++;}

        else if (a == ' ')
        {f.at(29).f++;}
    }
}

// sorts the frequencies in decreasing order
void sort(vector<Freq> &f)
{
    int i, j;
    int n = f.size();
    for (j = 0; j < n - 1; j++)
    {
        int max_val = j;
        for (i = j + 1; i < n; i++)
        {
            if (f.at(i).f > f.at(max_val).f)
            {max_val = i;}
        }
    
    
        if (max_val != j)
        {
            char tempc = f.at(j).c;
            float tempf = f.at(j).f;
     
            f.at(j).c = f.at(max_val).c;
            f.at(j).f = f.at(max_val).f;

            f.at(max_val).c = tempc;
            f.at(max_val).f = tempf;
        }
    }
}

// prints a column (or any vector of chars)
void print_col (vector<char> &c)
{
    for (unsigned i = 0; i < c.size(); i++)
    {
        cout << c.at(i);
    }
}

// swaps the characters in c with the corresponding characters in key
void swap_chars(vector<char> &c, vector<char> key)
{
    int k = -1;
    for (unsigned i = 0; i < c.size(); i++)
    {
        for (int j = 0; j < 26; j++)
        {
            if (c.at(i) == 'a' + j || c.at(i) == 'A' + j)
            {
                k = j;
            }
        }
        
        if (k >= 0)
        {c.at(i) = key.at(k);}
    
        else if (c.at(i) == ',')
        {c.at(i) = key.at(26);}
  
        else if (c.at(i) == '.')
        {c.at(i) = key.at(27);}

        else if (c.at(i) == '!')
        {c.at(i) = key.at(28);}

        else if (c.at(i) == ' ')
        {c.at(i) = key.at(29);}

        k = -1;
    }
}

void capitalize(vector<char> &c)
{
    for (unsigned i = 0; i < c.size(); i++)
    {
        if (i == 0)
        {c.at(i) = toupper(c.at(i));}
        else if (c.at(i-1) == '\n')
        {c.at(i) = toupper(c.at(i));}
    }
}

int main()
{
    vector<char> cipher;
    vector<char> col0;
    vector<char> col1;
    vector<char> col2;
    vector<char> col3;

    unsigned size = 0;
    ifstream fin;
    string input_file = "problem1.enc";

    fin.open(input_file.c_str());

    // Place characters from problem1.enc to cipher vector
    if (!fin.is_open())
    {cout << "Error: file was not opened." << endl; return 1;}

    while (!fin.eof())
    {
        char chara;
        chara = fin.get();
        cipher.push_back(chara);
    }
    
    fin.close();

    cipher.pop_back();		// removes last endline character
    size = cipher.size();

    // test: outputs the vector obtained from problem1.enc (ciphertext)
    /*
    for (unsigned i = 0; i < size; i++)
    {
	if (cipher.at(i) == '\n')
        cout << '0';

        else if (cipher.at(i) == ' ')
        cout << '1';

        else 
        cout << cipher.at(i);
    }
    cout << endl;
    cout  << "size of cipher: " << size << endl;
    */

    // Place characters in individual columns
    for (unsigned i = 0; i < size; i++)
    {
        char chara = cipher.at(i);

        if (i < size / 4)
        {col0.push_back(chara);}

        else if (i >= size / 4 && i < size / 2)
        {col1.push_back(chara);}

        else if (i >= size / 2 && i < size * 3 / 4)
        {col2.push_back(chara);}

        else if (i >= size * 3 / 4 && i < size)
        {col3.push_back(chara);}

        /*
	if (i % 4 == 0)
        {col0.push_back(chara);}

        else if (i % 4 == 1)
        {col1.push_back(chara);}

        else if (i % 4 == 2)
        {col2.push_back(chara);}

        else if (i % 4 == 3)
        {col3.push_back(chara);}
        */
    }

    // test: prints out contents of each column 
    /*    
    cout << endl << "Column 0 contents:" << endl;
    print_col(col0); cout << endl;

    cout << endl << "Column 1 contents:" << endl;
    print_col(col1); cout << endl;

    cout << endl << "Column 2 contents:" << endl;
    print_col(col2); cout << endl;

    cout << endl << "Column 3 contents:" << endl;
    print_col(col3); cout << endl;
    */

    // swapping columns
    vector<char> new_col0 = col1;	// 0 <- 1
    vector<char> new_col1 = col2;       // 1 <- 2
    vector<char> new_col2 = col3;       // 2 <- 3
    vector<char> new_col3 = col0;       // 3 <- 0
    vector<char> cipher2;               // vector to place new columns

    // test: prints out contents of swapped columns
    /*
    cout << endl << "New Column 0 contents:" << endl;
    print_col(new_col0); cout << endl;

    cout << endl << "New Column 1 contents:" << endl;
    print_col(new_col1); cout << endl;

    cout << endl << "New Column 2 contents:" << endl;
    print_col(new_col2); cout << endl;

    cout << endl << "New Column 3 contents:" << endl;
    print_col(new_col3); cout << endl;
    */

    for (unsigned i = 0; i < new_col0.size(); i++)
    {
        cipher2.push_back(new_col0.at(i));
        cipher2.push_back(new_col1.at(i));
        cipher2.push_back(new_col2.at(i));
        cipher2.push_back(new_col3.at(i));
    } 

    // test: prints out contents of new ciphertext with swapped columns
    /*
    cout << endl << "New Ciphertext:" << endl;
    print_col(cipher2); cout << endl << endl;
    */
   
    vector<Freq> freq_vec(30);		// vector to place all frequencies
    // initialize vector with characters
    for (unsigned i = 0; i < 26; i++)
    {
        freq_vec.at(i).c = 'a' + i;
        freq_vec.at(i).f = 0;
    }

    freq_vec.at(26).c = ',';
    freq_vec.at(26).f = 0;

    freq_vec.at(27).c = '.';
    freq_vec.at(27).f = 0;

    freq_vec.at(28).c = '!';
    freq_vec.at(28).f = 0;

    freq_vec.at(29).c = ' ';
    freq_vec.at(29).f = 0;

    // test: testing out struct vector and initialization
    /*
    cout << "Outputting frequencies test:" << endl;
    print_freq(freq_vec); cout << endl;
    */
   
    count(freq_vec, cipher2);		// calculate frequencies

    // test: outputting frequencies of each character from cipher2
    /*
    cout << "Outputting frequencies:" << endl;
    print_freq(freq_vec); cout << endl;
    */

    sort(freq_vec);			// sort frequencies

    // test: outputs sorted frequencies
    /*
    cout << "Outputting sorted frequencies:" << endl;
    print_freq(freq_vec); cout << endl;
    */
   
    // test: outputs the key for deciphering
    /*
    cout << "Outputting key:" << endl;
    print_col(key); cout << endl;
    */

    swap_chars(cipher2, key);		// decipher
    capitalize(cipher2);		// capitalize letters
    
    // test: outputs final plaintext
    
    cout << "Outputting plaintext:" << endl;
    print_col(cipher2); cout << endl;
    

    return 0;
}
 
