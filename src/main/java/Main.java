
public class Main {

    public static void main(String[] args){

        String input = "<?xml version='1.0' encoding='UTF-8'?><timetable station='Bingen(Rhein) Hbf'><s id=\"280415789363117741-2307031956-1\"><tl f=\"N\" t=\"p\" o=\"V6RB\" c=\"RB\" n=\"29663\"/><dp pt=\"2307031956\" pp=\"202\" l=\"65\" ppth=\"Münster-Sarmsheim|Laubenheim(Nahe)|Langenlonsheim|Bretzenheim(Nahe)|Bad Kreuznach\"/></s><s id=\"5035399661838689745-2307031804-6\"><tl f=\"N\" t=\"p\" o=\"V6RE\" c=\"RE\" n=\"29664\"/><ar pt=\"2307031912\" pp=\"202\" l=\"17\" ppth=\"Kaiserslautern Hbf|Winnweiler|Rockenhausen|Bad Münster a Stein|Bad Kreuznach\"/></s><s id=\"-3272436140147206876-2307031832-10\"><tl f=\"N\" t=\"p\" o=\"TR\" c=\"RB\" n=\"25386\"/><ar pt=\"2307031908\" pp=\"103\" l=\"26\" ppth=\"Mainz Hbf|Mainz-Mombach|Budenheim|Uhlerborn|Heidesheim(Rheinhess)|Ingelheim|Gau Algesheim|Bingen-Gaulsheim|Bingen(Rhein) Stadt\"/></s><s id=\"-5110963107092006829-2307031736-17\"><tl f=\"N\" t=\"p\" o=\"801566\" c=\"RB\" n=\"12747\"/><ar pt=\"2307031904\" pp=\"203\" l=\"65\" ppth=\"Kaiserslautern Hbf|Hochspeyer|Enkenbach|Münchweiler(Alsenz)|Winnweiler|Imsweiler|Rockenhausen|Alsenz|Hochstätten(Pfalz)|Altenbamberg|Bad Münster a Stein|Bad Kreuznach|Bretzenheim(Nahe)|Langenlonsheim|Laubenheim(Nahe)|Münster-Sarmsheim\"/></s><s id=\"-6751124335096260720-2307031919-4\"><tl f=\"N\" t=\"p\" o=\"801566\" c=\"RB\" n=\"12749\"/><ar pt=\"2307031932\" pp=\"203\" l=\"65\" ppth=\"Langenlonsheim|Laubenheim(Nahe)|Münster-Sarmsheim\"/></s><s id=\"-5513601900781325766-2307031825-7\"><tl f=\"N\" t=\"p\" o=\"V6RE\" c=\"RE\" n=\"29544\"/><ar pt=\"2307031921\" pp=\"102\" l=\"2\" ppth=\"Frankfurt(Main)Hbf|Frankfurt(M) Flughafen Regionalbf|Rüsselsheim|Mainz Hbf|Ingelheim|Gau Algesheim\"/><dp pt=\"2307031922\" pp=\"102\" l=\"2\" ppth=\"Bacharach|Oberwesel|Boppard Hbf|Koblenz Hbf\"/></s><s id=\"-2850414882283793971-2307031650-36\"><tl f=\"N\" t=\"p\" o=\"TR\" c=\"RB\" n=\"25433\"/><dp pt=\"2307031924\" pp=\"201\" l=\"26\" ppth=\"Bingen(Rhein) Stadt|Bingen-Gaulsheim|Gau Algesheim|Ingelheim|Heidesheim(Rheinhess)|Uhlerborn|Budenheim|Mainz-Mombach|Mainz Hbf\"/></s><s id=\"-4990049232902877541-2307031903-10\"><tl f=\"N\" t=\"p\" o=\"TR\" c=\"RB\" n=\"25438\"/><ar pt=\"2307031934\" pp=\"103\" l=\"26\" ppth=\"Mainz Hbf|Mainz-Mombach|Budenheim|Uhlerborn|Heidesheim(Rheinhess)|Ingelheim|Gau Algesheim|Bingen-Gaulsheim|Bingen(Rhein) Stadt\"/><dp pt=\"2307031939\" pp=\"103\" l=\"26\" ppth=\"Trechtingshausen|Niederheimbach|Bacharach|Oberwesel|St Goar|Boppard-Hirzenach|Boppard-Bad Salzig|Boppard Hbf|Spay|Rhens|Koblenz Hbf|Koblenz Stadtmitte|Koblenz-Lützel|Mülheim-Kärlich|Weißenthurm|Andernach|Namedy|Brohl|Bad Breisig|Sinzig(Rhein)|Remagen|Oberwinter|Rolandseck|Bonn-Mehlem|Bonn-Bad Godesberg|Bonn UN Campus|Bonn Hbf|Roisdorf|Sechtem|Brühl|Hürth-Kalscheuren|Köln Süd|Köln West|Köln Hbf|Köln Messe/Deutz\"/></s><s id=\"2805882215028788438-2307031902-5\"><tl f=\"N\" t=\"p\" o=\"800640\" c=\"RE\" n=\"4267\"/><ar pt=\"2307031946\" pp=\"101\" l=\"2\" ppth=\"Koblenz Hbf|Boppard Hbf|Oberwesel|Bacharach\"/><dp pt=\"2307031947\" pp=\"101\" l=\"2\" ppth=\"Ingelheim|Mainz Hbf|Mainz Römisches Theater|Mainz-Bischofsheim|Rüsselsheim|Frankfurt(M) Flughafen Regionalbf|Frankfurt-Niederrad|Frankfurt(Main)Hbf\"/></s></timetable>";

        convertString(input);

    }

    public static String convertString(String s){
        String[] strings = s.split("</s>");
        boolean dateWritten = false;
        for (int i = 0; i < strings.length- 1; i++){
            String train = strings[i].substring(strings[i].indexOf("id=\"")+4, strings[i].indexOf("\"", strings[i].indexOf("id=\"")+4)) + ";";
            train += strings[i].substring(strings[i].indexOf("c=\"")+3, strings[i].indexOf("\"", strings[i].indexOf("c=\"")+3));
            train += strings[i].substring(strings[i].indexOf("l=\"")+3, strings[i].indexOf("\"", strings[i].indexOf("l=\"")+3)) + ";";
            if (strings[i].contains("<ar")) {
                train += strings[i].substring(strings[i].indexOf("ar pt=") + 7, strings[i].indexOf("ar pt=") + 13) + ";";
                train += strings[i].substring(strings[i].indexOf("ar pt=") + 13, strings[i].indexOf("ar pt=") + 17) + ";";
                dateWritten = true;
            }
            if (strings[i].contains("<dp")){
                if (!dateWritten) {
                    train += strings[i].substring(strings[i].indexOf("dp pt=") + 7, strings[i].indexOf("dp pt=")+13) + ";";
                    train += "NULL;";
                }
                train += strings[i].substring(strings[i].indexOf("dp pt=") + 13, strings[i].indexOf("dp pt=")+17) + ";";
            }
            dateWritten = false;
            System.out.println(train);
        }
        return "NEWDATA"+s;
    }

}
