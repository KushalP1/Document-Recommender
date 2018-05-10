#include<bits/stdc++.h>
using namespace std;
int smat[13][597];


//function to map Researcher no. to index
void mapR(int ind){
    string s;
    int k;
    if(ind<79) {
        s="P00-10";
        k=ind+1;
    }
    else if(ind<149) {
        s="P01-10";
        k=ind-79+1;
    }
    else if(ind<214) {
        s="P02-10";
        k=ind-149+1;
    }
    else if(ind<285){
        s="P03-10";
        k=ind-214+1;
    }
    else if(ind<373){
        s="P04-10";
        k=ind-285+1;
    }
    else if(ind<450){
        s="P05-10";
        k=ind-373+1;
    }
    else{
        s="P06-10";
        k=ind-450+1;
    }

    cout<<s<<k<<endl;
}



//function to read smatrix
void smatR(){
    ifstream inFile;
    inFile.open("smatrix.txt");
    char line[10000];
    for(int i=1;i<=13;i++){
        inFile.getline(line,10000);
         for(int j=1;j<=597;j++)
        {
            //cout<<line[2*j-2]<<" ";
          smat[i-1][j-1]=line[2*j-2]-'0';
        }
        //cout<<endl;
    }
    inFile.close();
}

//function to write smatrix
void smatW(){
    ofstream outFile;
    outFile.open("smatrix.txt");
    for(int i=0;i<13;i++){
        for(int j=0;j<597;j++){
            outFile<<smat[i][j]<<" ";
           // if(smat[i][j]==1) cout<<i<<" "<<j<<endl;
        }
        outFile<<"\n";
    }
    outFile.close();

}
//to check similarity btw papers and researcher ,update and recommend
void simR_P(int resN){
    ifstream inFile;
    inFile.open("597.txt");
    vector < pair <double,int> > matR_P[13];
    char line[100000];
    for(int i=1;i<=13;i++)
      {
        inFile.getline(line,100000);
        char *tok=strtok(line,", ");
        matR_P[i-1].push_back(make_pair(atof(tok),0));
        //cout<<mares[i-1][0]<<" ";
        for(int j=1;j<597;j++)
        {
          tok=strtok(NULL,", ");
          matR_P[i-1].push_back(make_pair(atof(tok),j));
        }
      }
    inFile.close();
    sort(matR_P[resN-1].begin(),matR_P[resN-1].end());
    int flag=0;
    for(int i=596;i>=0;i--){
        if(smat[resN-1][matR_P[resN-1][i].second]==0){
            cout<<"Also read ";
            mapR(matR_P[resN-1][i].second);
            smat[resN-1][matR_P[resN-1][i].second]=1;
            //cout<<smat[10][53]<<endl;
            flag=1;
            break;
        }
    }
    if(flag==0) cout<<"All possible papers read"<<endl;

}



int main(int argc, char** argv)
{
 int researcher_no = atoi(argv[1]);
 char x;
 char lineomat[1000];
 double mares[100][100];
 ifstream inFile1;
 inFile1.open("/home/kushal/Desktop/IR/mat.txt");
 char line[1000];
 for(int i=1;i<=13;i++)
  {
    inFile1.getline(line,1000);
   char *tok=strtok(line,", ");
    mares[i-1][0]=atof(tok);
    for(int j=2;j<=13;j++)
    {
      tok=strtok(NULL,", ");
      mares[i-1][j-1]=atof(tok);
    }
  }

  inFile1.close();

  smatR();

  //cout<<researcher_no<<" "<<smat[researcher_no-1][53]<<endl;
  double maxi=INT_MIN;
  int simRes=0;
  for(int i=0;i<13;i++){
    if(maxi<mares[researcher_no-1][i] && i!=researcher_no-1){
        maxi=mares[researcher_no-1][i];
        simRes=i;
    }
  }
  int flag=0;
  for(int i=0;i<597;i++){
    if(smat[researcher_no-1][i]==0 && smat[simRes][i]==1){
        cout<<"Also Read ";
        mapR(i);
        smat[researcher_no-1][i]=1;
        flag=1;
        break;
        }
    }
    if(flag==0) simR_P(researcher_no);

    smatW();

}


/*
 int max=INT_MIN;
 for(int i=0;i<13;i++)
  if(max<mares[researcher_no-1][i]&&(i!=researcherno-1))
    { max=mares[researcher_no-1][i];
      pos=i;
     }
  if(max==0)
  {

    //check the similarity within the row



   }




  for(int i=1;i<=pos;i++)
  { inFile.getline(line,597);
     for(int i=0;i<strlen(line);i++)
       if(line[i]=='1')
        {    inFile2.open("smatrix.txt");
             for(int i=1;i<=researcher_no;i++)
                 inFile2.getline(line1,597);
             if(line1[i]=='0')
             {   line1[i]='1';

                 //recommend and update the file with line1 in the researcher_no row





                  break;
              }
       }
      count++;
      mares[researcher_no-1][pos]=0;
      goto checknext;
/* while (inFile >> x) {
   cout<<x;*/
