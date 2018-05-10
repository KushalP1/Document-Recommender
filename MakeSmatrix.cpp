#include <bits/stdc++.h>
using namespace std;

int main(){
    ofstream file;
	cout << "Hello" << endl;
    file.open("smatrix.txt");
    for(int i=0;i<13;i++){
        for(int j=0;j<597;j++){
            file<<"0 ";
        }
        file<<"\n";
    }

}
