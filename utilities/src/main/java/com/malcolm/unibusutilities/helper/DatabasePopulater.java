package com.malcolm.unibusutilities.helper;

import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabasePopulater {

    public static void populateHolidayTimetable(SupportSQLiteDatabase database) {
        database.execSQL("INSERT INTO `Timetable` (id,'IMS Eastney (Departures)','Langstone Campus (for Departures only)'" +
                ",'Locksway Road (for Milton Park)','Goldsmith Avenue (adj Lidi)','Goldsmith Avenue (opp Fratton Station)'" +
                ",'Winston Churchill Avenue (adj Ibis Hotel)','Cambridge Road (adj Student Union for Arrivals only)'" +
                ",'Cambridge Road (adj Nuffield Building)','Winston Churchill Avenue (adj Law Courts)'" +
                ",'Goldsmith Avenue (adj Fratton Station)','Goldsmith Avenue (opp Lidl)','Goldsmith Avenue (adj Milton Park)'" +
                ",'IMS Eastney','Langstone Campus (for Arrivals only)')" +
                " VALUES (1,NULL,28800,29040,29280,29400,29580,29760,30000,30300,30480,30600,30780,NULL,30960),\n" +
                " (2,NULL,31200,31440,31680,31800,31980,32160,32400,32700,32880,33000,33180,NULL,33360),\n" +
                " (3,NULL,33600,33840,34080,34200,34380,34560,34800,35100,35280,35400,35580,NULL,35760),\n" +
                " (4,NULL,36000,36240,36480,36600,36780,36960,37200,37500,37680,37800,37980,NULL,38160),\n" +
                " (5,NULL,38400,38640,38880,39000,39180,39360,39600,39900,40080,40200,40380,NULL,40560),\n" +
                " (6,NULL,40800,41040,41280,41400,41580,41760,42000,42300,42480,42600,42780,NULL,42960),\n" +
                " (7,NULL,43200,43440,43680,43800,43980,44160,44400,44700,44880,45000,45180,NULL,45360),\n" +
                " (8,NULL,45600,45840,46080,46200,46380,46560,46800,47100,47280,47400,47580,NULL,47760),\n" +
                " (9,NULL,48000,48240,48480,48600,48780,48960,49200,49500,49680,49800,49980,NULL,50160),\n" +
                " (10,NULL,50400,50640,50880,51000,51180,51360,51600,51900,52080,52200,52380,NULL,52560),\n" +
                " (11,NULL,52800,53040,53280,53400,53580,53760,54000,54300,54480,54600,54780,NULL,54960),\n" +
                " (12,NULL,55200,55440,55680,55800,55980,56160,56400,56700,56880,57000,57180,NULL,57360),\n" +
                " (13,NULL,57600,57840,58080,58200,58380,58560,58800,59100,59280,59400,59580,NULL,59760),\n" +
                " (14,NULL,61200,61440,61680,61800,61980,62160,62400,62700,62880,63000,63180,NULL,63360);\n");
    }

    public static void populateNormalTimetable(SupportSQLiteDatabase database) {
        database.execSQL("INSERT INTO `Timetable` (id,'IMS Eastney (Departures)','Langstone Campus (for Departures only)'" +
                ",'Locksway Road (for Milton Park)','Goldsmith Avenue (adj Lidi)','Goldsmith Avenue (opp Fratton Station)'" +
                ",'Winston Churchill Avenue (adj Ibis Hotel)','Cambridge Road (adj Student Union for Arrivals only)'" +
                ",'Cambridge Road (adj Nuffield Building)','Winston Churchill Avenue (adj Law Courts)'" +
                ",'Goldsmith Avenue (adj Fratton Station)','Goldsmith Avenue (opp Lidl)','Goldsmith Avenue (adj Milton Park)'" +
                ",'IMS Eastney','Langstone Campus (for Arrivals only)')" +
                " VALUES (1,NULL,27600,27780,28020,28140,28380,28500,28800,29100,29280,29400,29640,NULL,29820),\n" +
                " (2,NULL,28800,28980,29220,29340,29580,29700,30000,30300,30480,30600,30840,NULL,31020),\n" +
                " (3,NULL,30000,30180,30420,30540,30780,30900,31200,31500,31680,31800,32040,NULL,32220),\n" +
                " (4,NULL,31200,31380,31620,31740,31980,32100,32400,32700,32880,33000,33240,NULL,33420),\n" +
                " (5,NULL,32400,32580,32820,32940,33180,33300,33600,33900,34080,34200,34440,NULL,34620),\n" +
                " (6,NULL,NULL,NULL,33300,33480,33600,33900,NULL,34080,34320,34500,34690,35400,NULL),\n" +
                " (7,NULL,33600,33780,34020,34140,34380,34500,34800,35100,35280,35400,35640,NULL,35820),\n" +
                " (8,NULL,34800,34980,35220,35340,35580,35700,36000,36300,36480,36600,36840,NULL,37020),\n" +
                " (9,NULL,36000,36180,36420,36540,36780,36900,37200,37500,37680,37800,38040,NULL,38220),\n" +
                " (10,36240,NULL,36720,36900,37080,37200,37500,NULL,37680,37920,38100,38280,39000,NULL),\n" +
                " (11,NULL,37200,37380,37620,37740,37980,38100,38400,38700,38880,39000,39240,NULL,39420),\n" +
                " (12,NULL,38400,38580,38820,38940,39180,39300,39600,39900,40080,40200,40440,NULL,40620),\n" +
                " (13,NULL,39600,39780,40020,40140,40380,40500,40800,41100,41280,41400,41640,NULL,41820),\n" +
                " (14,39840,NULL,40320,40500,40680,40800,41100,NULL,41280,41520,41700,41870,43200,43920),\n" +
                " (15,NULL,40800,40980,41220,41340,41580,41700,42000,42300,42480,42600,42840,NULL,43020),\n" +
                " (16,NULL,42000,42180,42420,42540,42780,42900,43200,43500,43680,43800,44040,NULL,44220),\n" +
                " (17,NULL,43200,43380,43620,43740,43980,44100,44400,44700,44880,45000,45240,NULL,45420),\n" +
                " (18,NULL,44400,44580,44820,44940,45180,45300,45600,45900,46080,46200,46440,NULL,46620),\n" +
                " (19,NULL,45600,45780,46020,46140,46380,46500,46800,47100,47280,47400,47640,NULL,47820),\n" +
                " (20,NULL,45900,46320,46500,46680,46800,47100,NULL,47280,47520,47700,47880,48600,NULL),\n" +
                " (21,NULL,46800,46980,47220,47340,47580,47700,48000,48300,48480,48600,48840,NULL,49020),\n" +
                " (22,NULL,48000,48180,48420,48540,48780,48900,49200,49500,49680,49800,50040,NULL,50220),\n" +
                " (23,NULL,49200,49380,49620,49740,49980,50100,50400,50700,50880,51000,51240,NULL,51420),\n" +
                " (24,49140,NULL,49620,49800,49980,50100,50400,NULL,50580,50820,51000,51180,52200,52920),\n" +
                " (25,NULL,50400,50580,50820,50940,51180,51300,51600,51900,52080,52200,52440,NULL,52620),\n" +
                " (26,NULL,51600,51780,52020,52140,52380,52500,52800,53100,53280,53400,53640,NULL,53820),\n" +
                " (27,NULL,52800,52980,53220,53340,53580,53700,54000,54300,54480,54600,54840,NULL,55020),\n" +
                " (28,NULL,54000,54180,54420,54540,54780,54900,55200,55500,55680,55800,56040,NULL,56220),\n" +
                " (29,NULL,54900,55320,55500,55680,55800,56100,NULL,56280,56520,56700,56880,57600,NULL),\n" +
                " (30,NULL,55200,55380,55620,55740,55980,56100,56400,56700,56880,57000,57240,NULL,57420),\n" +
                " (31,NULL,56400,56580,56820,56940,57180,57300,57600,57900,58080,58200,58440,NULL,58620),\n" +
                " (32,NULL,57600,57780,58020,58140,58380,58500,58800,59100,59280,59400,59640,NULL,59820),\n" +
                " (33,58140,NULL,58620,58800,58980,59100,59400,NULL,59580,59820,60000,60180,60900,NULL),\n" +
                " (34,NULL,58800,58980,59220,59340,59580,59700,60000,60300,60480,60600,60840,NULL,61020),\n" +
                " (35,NULL,60000,60180,60420,60540,60780,60900,61200,61500,61680,61800,62040,NULL,62220),\n" +
                " (36,NULL,61200,61380,61620,61740,61980,62100,62400,62700,62880,63000,63240,NULL,63420),\n" +
                " (37,61740,NULL,62220,62400,62580,62700,63000,NULL,63180,63420,63600,63780,NULL,64380),\n" +
                " (38,NULL,62400,62580,62820,62940,63180,63300,63600,63900,64080,64200,64440,NULL,64620),\n" +
                " (39,NULL,63600,63780,64020,64140,64380,64500,64800,65100,65280,65400,65640,NULL,65820),\n" +
                " (40,NULL,64800,64980,65220,65340,65580,65700,66000,66300,66480,66600,66840,NULL,67020),\n" +
                " (41,NULL,66000,66180,66420,66540,66780,66900,67200,67500,67680,67800,68040,NULL,68220),\n" +
                " (42,NULL,67200,67380,67620,67740,67980,68100,68400,68700,68880,69000,69240,NULL,69420),\n" +
                " (43,NULL,68400,68580,68820,68940,69180,69300,69600,69900,70080,70200,70440,NULL,70620),\n" +
                " (44,NULL,74400,74580,74820,74940,75180,75300,75600,75900,76080,76200,76440,NULL,76620),\n" +
                " (45,NULL,76800,76980,77220,77340,77580,77700,78000,78300,78480,78600,78840,NULL,79020),\n" +
                " (46,NULL,79200,79380,79620,79740,79980,80100,80400,80700,80880,81000,81240,NULL,81420),\n" +
                " (47,NULL,81600,81780,82020,82140,82380,82500,82800,83100,83280,83400,83640,NULL,83820),\n" +
                " (48,NULL,NULL,NULL,NULL,NULL,NULL,NULL,600,780,900,1020,1200,NULL,1500);");
    }

    public static void populateWeekendTimetable(SupportSQLiteDatabase database) {
        database.execSQL("INSERT INTO `Timetable` (id,'IMS Eastney (Departures)','Langstone Campus (for Departures only)'" +
                ",'Locksway Road (for Milton Park)','Goldsmith Avenue (adj Lidi)','Goldsmith Avenue (opp Fratton Station)'" +
                ",'Winston Churchill Avenue (adj Ibis Hotel)','Cambridge Road (adj Student Union for Arrivals only)'" +
                ",'Cambridge Road (adj Nuffield Building)','Winston Churchill Avenue (adj Law Courts)'" +
                ",'Goldsmith Avenue (adj Fratton Station)','Goldsmith Avenue (opp Lidl)','Goldsmith Avenue (adj Milton Park)'" +
                ",'IMS Eastney','Langstone Campus (for Arrivals only)')" +
                " VALUES (1,NULL,36000,36240,36480,36600,36780,36960,37200,37500,37680,37800,37980,NULL,NULL),\n" +
                " (2,NULL,38400,38640,38880,39000,39180,39360,39600,39900,40080,40200,40380,NULL,NULL),\n" +
                " (3,NULL,40800,41040,41280,41400,41580,41760,42000,42300,42480,42600,42780,NULL,NULL),\n" +
                " (4,NULL,43200,43440,43680,43800,43980,44160,44400,44700,44880,45000,45180,NULL,45360),\n" +
                " (5,NULL,48000,48240,48480,48600,48780,48960,49200,49500,49680,49800,49980,NULL,NULL),\n" +
                " (6,NULL,50400,50640,50880,51000,51180,51360,51600,51900,52080,52200,52380,NULL,NULL),\n" +
                " (7,NULL,52800,53040,53280,53400,53580,53760,54000,54300,54480,54600,54780,NULL,NULL),\n" +
                " (8,NULL,55200,55440,55680,55800,55980,56160,56400,56700,56880,57000,57180,NULL,57360),\n" +
                " (9,NULL,58800,59040,59280,59400,59580,59760,60000,60300,60480,60600,60780,NULL,NULL),\n" +
                " (10,NULL,61200,61440,61680,61800,61980,62160,62400,62700,62880,63000,63180,NULL,NULL),\n" +
                " (11,NULL,63600,63840,64080,64200,64380,64560,64800,65100,65280,65400,65580,NULL,65760);");
    }

    public static void populateWednesdayTimetable(SupportSQLiteDatabase database) {
        database.execSQL("INSERT INTO `Timetable` (id,'IMS Eastney (Departures)','Langstone Campus (for Departures only)'" +
                ",'Locksway Road (for Milton Park)','Goldsmith Avenue (adj Lidi)','Goldsmith Avenue (opp Fratton Station)'" +
                ",'Winston Churchill Avenue (adj Ibis Hotel)','Cambridge Road (adj Student Union for Arrivals only)'" +
                ",'Cambridge Road (adj Nuffield Building)','Winston Churchill Avenue (adj Law Courts)'" +
                ",'Goldsmith Avenue (adj Fratton Station)','Goldsmith Avenue (opp Lidl)','Goldsmith Avenue (adj Milton Park)'" +
                ",'IMS Eastney','Langstone Campus (for Arrivals only)')" +
                " VALUES (1,NULL,27600,27780,28020,28140,28380,28500,28800,29100,29280,29400,29640,NULL,29820),\n" +
                " (2,NULL,28800,28980,29220,29340,29580,29700,30000,30300,30480,30600,30840,NULL,31020),\n" +
                " (3,NULL,30000,30180,30420,30540,30780,30900,31200,31500,31680,31800,32040,NULL,32220),\n" +
                " (4,NULL,31200,31380,31620,31740,31980,32100,32400,32700,32880,33000,33240,NULL,33420),\n" +
                " (5,NULL,32400,32580,32820,32940,33180,33300,33600,33900,34080,34200,34440,NULL,34620),\n" +
                " (6,NULL,NULL,NULL,33300,33480,33600,33900,NULL,34080,34320,34500,34690,35400,NULL),\n" +
                " (7,NULL,33600,33780,34020,34140,34380,34500,34800,35100,35280,35400,35640,NULL,35820),\n" +
                " (8,NULL,34800,34980,35220,35340,35580,35700,36000,36300,36480,36600,36840,NULL,37020),\n" +
                " (9,NULL,36000,36180,36420,36540,36780,36900,37200,37500,37680,37800,38040,NULL,38220),\n" +
                " (10,36240,NULL,36720,36900,37080,37200,37500,NULL,37680,37920,38100,38280,39000,NULL),\n" +
                " (11,NULL,37200,37380,37620,37740,37980,38100,38400,38700,38880,39000,39240,NULL,39420),\n" +
                " (12,NULL,38400,38580,38820,38940,39180,39300,39600,39900,40080,40200,40440,NULL,40620),\n" +
                " (13,NULL,39600,39780,40020,40140,40380,40500,40800,41100,41280,41400,41640,NULL,41820),\n" +
                " (14,39840,NULL,40320,40500,40680,40800,41100,NULL,41280,41520,41700,41870,42600,NULL),\n" +
                " (15,NULL,40800,40980,41220,41340,41580,41700,42000,42300,42480,42600,42840,NULL,43020),\n" +
                " (16,NULL,42000,42180,42420,42540,42780,42900,43200,43500,43680,43800,44040,NULL,44220),\n" +
                " (17,NULL,43200,43380,43620,43740,43980,44100,44400,44700,44880,45000,45240,NULL,45420),\n" +
                " (18,43440,NULL,43920,44100,44280,44400,44700,NULL,44880,45120,45300,45460,46800,47520),\n" +
                " (19,NULL,44400,44580,44820,44940,45180,45300,45600,45900,46080,46200,46440,NULL,46620),\n" +
                " (20,NULL,45600,45780,46020,46140,46380,46500,46800,47100,47280,47400,47640,NULL,47820),\n" +
                " (21,NULL,46800,46980,47220,47340,47580,47700,48000,48300,48480,48600,48840,NULL,49020),\n" +
                " (22,NULL,48000,48180,48420,48540,48780,48900,49200,49500,49680,49800,50040,NULL,50220),\n" +
                " (23,NULL,49200,49380,49620,49740,49980,50100,50400,50700,50880,51000,51240,NULL,51420),\n" +
                " (24,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),\n" +
                " (25,NULL,50400,50580,50820,50940,51180,51300,51600,51900,52080,52200,52440,NULL,52620),\n" +
                " (26,NULL,51600,51780,52020,52140,52380,52500,52800,53100,53280,53400,53640,NULL,53820),\n" +
                " (27,NULL,52800,52980,53220,53340,53580,53700,54000,54300,54480,54600,54840,NULL,55020),\n" +
                " (28,NULL,54000,54180,54420,54540,54780,54900,55200,55500,55680,55800,56040,NULL,56220),\n" +
                " (29,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),\n" +
                " (30,NULL,55200,55380,55620,55740,55980,56100,56400,56700,56880,57000,57240,NULL,57420),\n" +
                " (31,NULL,56400,56580,56820,56940,57180,57300,57600,57900,58080,58200,58440,NULL,58620),\n" +
                " (32,NULL,57600,57780,58020,58140,58380,58500,58800,59100,59280,59400,59640,NULL,59820),\n" +
                " (33,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),\n" +
                " (34,NULL,58800,58980,59220,59340,59580,59700,60000,60300,60480,60600,60840,NULL,61020),\n" +
                " (35,NULL,60000,60180,60420,60540,60780,60900,61200,61500,61680,61800,62040,NULL,62220),\n" +
                " (36,NULL,61200,61380,61620,61740,61980,62100,62400,62700,62880,63000,63240,NULL,63420),\n" +
                " (37,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),\n" +
                " (38,NULL,62400,62580,62820,62940,63180,63300,63600,63900,64080,64200,64440,NULL,64620),\n" +
                " (39,NULL,63600,63780,64020,64140,64380,64500,64800,65100,65280,65400,65640,NULL,65820),\n" +
                " (40,NULL,64800,64980,65220,65340,65580,65700,66000,66300,66480,66600,66840,NULL,67020),\n" +
                " (41,NULL,66000,66180,66420,66540,66780,66900,67200,67500,67680,67800,68040,NULL,68220),\n" +
                " (42,NULL,67200,67380,67620,67740,67980,68100,68400,68700,68880,69000,69240,NULL,69420),\n" +
                " (43,NULL,68400,68580,68820,68940,69180,69300,69600,69900,70080,70200,70440,NULL,70620),\n" +
                " (44,NULL,74400,74580,74820,74940,75180,75300,75600,75900,76080,76200,76440,NULL,76620),\n" +
                " (45,NULL,76800,76980,77220,77340,77580,77700,78000,78300,78480,78600,78840,NULL,79020),\n" +
                " (46,NULL,79200,79380,79620,79740,79980,80100,80400,80700,80880,81000,81240,NULL,81420),\n" +
                " (47,NULL,81600,81780,82020,82140,82380,82500,82800,83100,83280,83400,83640,NULL,83820),\n" +
                " (48,NULL,NULL,NULL,NULL,NULL,NULL,NULL,600,780,900,1020,1200,NULL,1500);");
    }

    public static void migrateHolidayTimetable(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE \"Timetable\" RENAME TO 'Timetable_Old';\n");
        database.execSQL("CREATE TABLE \"Timetable\" (\n" +
                "\t`id`\tINTEGER DEFAULT (null),\n" +
                "\t`IMS Eastney (Departures)`\tINTEGER DEFAULT (null),\n" +
                "\t`Langstone Campus (for Departures only)`\tINTEGER,\n" +
                "\t`Locksway Road (for Milton Park)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (adj Lidi)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (opp Fratton Station)`\tINTEGER,\n" +
                "\t`Winston Churchill Avenue (adj Ibis Hotel)`\tINTEGER,\n" +
                "\t`Cambridge Road (adj Student Union for Arrivals only)`\tINTEGER,\n" +
                "\t`Cambridge Road (adj Nuffield Building)`\tINTEGER,\n" +
                "\t`Winston Churchill Avenue (adj Law Courts)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (adj Fratton Station)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (opp Lidl)`\tINTEGER DEFAULT (null),\n" +
                "\t`Goldsmith Avenue (adj Milton Park)`\tINTEGER,\n" +
                "\t`IMS Eastney`\tINTEGER DEFAULT (null),\n" +
                "\t`Langstone Campus (for Arrivals only)`\tINTEGER,\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");\n");
        database.execSQL("INSERT INTO `Timetable` (id,'IMS Eastney (Departures)','Langstone Campus (for Departures only)'" +
                ",'Locksway Road (for Milton Park)','Goldsmith Avenue (adj Lidi)','Goldsmith Avenue (opp Fratton Station)'" +
                ",'Winston Churchill Avenue (adj Ibis Hotel)','Cambridge Road (adj Student Union for Arrivals only)'" +
                ",'Cambridge Road (adj Nuffield Building)','Winston Churchill Avenue (adj Law Courts)'" +
                ",'Goldsmith Avenue (adj Fratton Station)','Goldsmith Avenue (opp Lidl)','Goldsmith Avenue (adj Milton Park)'" +
                ",'IMS Eastney','Langstone Campus (for Arrivals only)')" +
                " VALUES (1,NULL,28800,29040,29280,29400,29580,29760,30000,30300,30480,30600,30780,NULL,30960),\n" +
                " (2,NULL,31200,31440,31680,31800,31980,32160,32400,32700,32880,33000,33180,NULL,33360),\n" +
                " (3,NULL,33600,33840,34080,34200,34380,34560,34800,35100,35280,35400,35580,NULL,35760),\n" +
                " (4,NULL,36000,36240,36480,36600,36780,36960,37200,37500,37680,37800,37980,NULL,38160),\n" +
                " (5,NULL,38400,38640,38880,39000,39180,39360,39600,39900,40080,40200,40380,NULL,40560),\n" +
                " (6,NULL,40800,41040,41280,41400,41580,41760,42000,42300,42480,42600,42780,NULL,42960),\n" +
                " (7,NULL,43200,43440,43680,43800,43980,44160,44400,44700,44880,45000,45180,NULL,45360),\n" +
                " (8,NULL,45600,45840,46080,46200,46380,46560,46800,47100,47280,47400,47580,NULL,47760),\n" +
                " (9,NULL,48000,48240,48480,48600,48780,48960,49200,49500,49680,49800,49980,NULL,50160),\n" +
                " (10,NULL,50400,50640,50880,51000,51180,51360,51600,51900,52080,52200,52380,NULL,52560),\n" +
                " (11,NULL,52800,53040,53280,53400,53580,53760,54000,54300,54480,54600,54780,NULL,54960),\n" +
                " (12,NULL,55200,55440,55680,55800,55980,56160,56400,56700,56880,57000,57180,NULL,57360),\n" +
                " (13,NULL,57600,57840,58080,58200,58380,58560,58800,59100,59280,59400,59580,NULL,59760),\n" +
                " (14,NULL,61200,61440,61680,61800,61980,62160,62400,62700,62880,63000,63180,NULL,63360);\n");
        database.execSQL(
                "DROP TABLE \"Timetable_Old\"\n");
    }

    public static void migrateNormalTimetable(SupportSQLiteDatabase database){
        database.execSQL("ALTER TABLE \"Timetable\" RENAME TO 'Timetable_Old';\n");
        database.execSQL("CREATE TABLE \"Timetable\" (\n" +
                "\t`id`\tINTEGER DEFAULT (null),\n" +
                "\t`IMS Eastney (Departures)`\tINTEGER DEFAULT (null),\n" +
                "\t`Langstone Campus (for Departures only)`\tINTEGER,\n" +
                "\t`Locksway Road (for Milton Park)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (adj Lidi)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (opp Fratton Station)`\tINTEGER,\n" +
                "\t`Winston Churchill Avenue (adj Ibis Hotel)`\tINTEGER,\n" +
                "\t`Cambridge Road (adj Student Union for Arrivals only)`\tINTEGER,\n" +
                "\t`Cambridge Road (adj Nuffield Building)`\tINTEGER,\n" +
                "\t`Winston Churchill Avenue (adj Law Courts)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (adj Fratton Station)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (opp Lidl)`\tINTEGER DEFAULT (null),\n" +
                "\t`Goldsmith Avenue (adj Milton Park)`\tINTEGER,\n" +
                "\t`IMS Eastney`\tINTEGER DEFAULT (null),\n" +
                "\t`Langstone Campus (for Arrivals only)`\tINTEGER,\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");\n");
        database.execSQL("INSERT INTO `Timetable` (id,'IMS Eastney (Departures)','Langstone Campus (for Departures only)'" +
                ",'Locksway Road (for Milton Park)','Goldsmith Avenue (adj Lidi)','Goldsmith Avenue (opp Fratton Station)'" +
                ",'Winston Churchill Avenue (adj Ibis Hotel)','Cambridge Road (adj Student Union for Arrivals only)'" +
                ",'Cambridge Road (adj Nuffield Building)','Winston Churchill Avenue (adj Law Courts)'" +
                ",'Goldsmith Avenue (adj Fratton Station)','Goldsmith Avenue (opp Lidl)','Goldsmith Avenue (adj Milton Park)'" +
                ",'IMS Eastney','Langstone Campus (for Arrivals only)')" +
                " VALUES (1,NULL,27600,27780,28020,28140,28380,28500,28800,29100,29280,29400,29640,NULL,29820),\n" +
                " (2,NULL,28800,28980,29220,29340,29580,29700,30000,30300,30480,30600,30840,NULL,31020),\n" +
                " (3,NULL,30000,30180,30420,30540,30780,30900,31200,31500,31680,31800,32040,NULL,32220),\n" +
                " (4,NULL,31200,31380,31620,31740,31980,32100,32400,32700,32880,33000,33240,NULL,33420),\n" +
                " (5,NULL,32400,32580,32820,32940,33180,33300,33600,33900,34080,34200,34440,NULL,34620),\n" +
                " (6,NULL,NULL,NULL,33300,33480,33600,33900,NULL,34080,34320,34500,34690,35400,NULL),\n" +
                " (7,NULL,33600,33780,34020,34140,34380,34500,34800,35100,35280,35400,35640,NULL,35820),\n" +
                " (8,NULL,34800,34980,35220,35340,35580,35700,36000,36300,36480,36600,36840,NULL,37020),\n" +
                " (9,NULL,36000,36180,36420,36540,36780,36900,37200,37500,37680,37800,38040,NULL,38220),\n" +
                " (10,36240,NULL,36720,36900,37080,37200,37500,NULL,37680,37920,38100,38280,39000,NULL),\n" +
                " (11,NULL,37200,37380,37620,37740,37980,38100,38400,38700,38880,39000,39240,NULL,39420),\n" +
                " (12,NULL,38400,38580,38820,38940,39180,39300,39600,39900,40080,40200,40440,NULL,40620),\n" +
                " (13,NULL,39600,39780,40020,40140,40380,40500,40800,41100,41280,41400,41640,NULL,41820),\n" +
                " (14,39840,NULL,40320,40500,40680,40800,41100,NULL,41280,41520,41700,41870,43200,43920),\n" +
                " (15,NULL,40800,40980,41220,41340,41580,41700,42000,42300,42480,42600,42840,NULL,43020),\n" +
                " (16,NULL,42000,42180,42420,42540,42780,42900,43200,43500,43680,43800,44040,NULL,44220),\n" +
                " (17,NULL,43200,43380,43620,43740,43980,44100,44400,44700,44880,45000,45240,NULL,45420),\n" +
                " (18,NULL,44400,44580,44820,44940,45180,45300,45600,45900,46080,46200,46440,NULL,46620),\n" +
                " (19,NULL,45600,45780,46020,46140,46380,46500,46800,47100,47280,47400,47640,NULL,47820),\n" +
                " (20,NULL,45900,46320,46500,46680,46800,47100,NULL,47280,47520,47700,47880,48600,NULL),\n" +
                " (21,NULL,46800,46980,47220,47340,47580,47700,48000,48300,48480,48600,48840,NULL,49020),\n" +
                " (22,NULL,48000,48180,48420,48540,48780,48900,49200,49500,49680,49800,50040,NULL,50220),\n" +
                " (23,NULL,49200,49380,49620,49740,49980,50100,50400,50700,50880,51000,51240,NULL,51420),\n" +
                " (24,49140,NULL,49620,49800,49980,50100,50400,NULL,50580,50820,51000,51180,52200,52920),\n" +
                " (25,NULL,50400,50580,50820,50940,51180,51300,51600,51900,52080,52200,52440,NULL,52620),\n" +
                " (26,NULL,51600,51780,52020,52140,52380,52500,52800,53100,53280,53400,53640,NULL,53820),\n" +
                " (27,NULL,52800,52980,53220,53340,53580,53700,54000,54300,54480,54600,54840,NULL,55020),\n" +
                " (28,NULL,54000,54180,54420,54540,54780,54900,55200,55500,55680,55800,56040,NULL,56220),\n" +
                " (29,NULL,54900,55320,55500,55680,55800,56100,NULL,56280,56520,56700,56880,57600,NULL),\n" +
                " (30,NULL,55200,55380,55620,55740,55980,56100,56400,56700,56880,57000,57240,NULL,57420),\n" +
                " (31,NULL,56400,56580,56820,56940,57180,57300,57600,57900,58080,58200,58440,NULL,58620),\n" +
                " (32,NULL,57600,57780,58020,58140,58380,58500,58800,59100,59280,59400,59640,NULL,59820),\n" +
                " (33,58140,NULL,58620,58800,58980,59100,59400,NULL,59580,59820,60000,60180,60900,NULL),\n" +
                " (34,NULL,58800,58980,59220,59340,59580,59700,60000,60300,60480,60600,60840,NULL,61020),\n" +
                " (35,NULL,60000,60180,60420,60540,60780,60900,61200,61500,61680,61800,62040,NULL,62220),\n" +
                " (36,NULL,61200,61380,61620,61740,61980,62100,62400,62700,62880,63000,63240,NULL,63420),\n" +
                " (37,61740,NULL,62220,62400,62580,62700,63000,NULL,63180,63420,63600,63780,NULL,64380),\n" +
                " (38,NULL,62400,62580,62820,62940,63180,63300,63600,63900,64080,64200,64440,NULL,64620),\n" +
                " (39,NULL,63600,63780,64020,64140,64380,64500,64800,65100,65280,65400,65640,NULL,65820),\n" +
                " (40,NULL,64800,64980,65220,65340,65580,65700,66000,66300,66480,66600,66840,NULL,67020),\n" +
                " (41,NULL,66000,66180,66420,66540,66780,66900,67200,67500,67680,67800,68040,NULL,68220),\n" +
                " (42,NULL,67200,67380,67620,67740,67980,68100,68400,68700,68880,69000,69240,NULL,69420),\n" +
                " (43,NULL,68400,68580,68820,68940,69180,69300,69600,69900,70080,70200,70440,NULL,70620),\n" +
                " (44,NULL,74400,74580,74820,74940,75180,75300,75600,75900,76080,76200,76440,NULL,76620),\n" +
                " (45,NULL,76800,76980,77220,77340,77580,77700,78000,78300,78480,78600,78840,NULL,79020),\n" +
                " (46,NULL,79200,79380,79620,79740,79980,80100,80400,80700,80880,81000,81240,NULL,81420),\n" +
                " (47,NULL,81600,81780,82020,82140,82380,82500,82800,83100,83280,83400,83640,NULL,83820),\n" +
                " (48,NULL,NULL,NULL,NULL,NULL,NULL,NULL,600,780,900,1020,1200,NULL,1500);");
        database.execSQL("DROP TABLE \"Timetable_Old\"\n");
    }

    public static void migrateWednesdayTimetable(SupportSQLiteDatabase database){
        database.execSQL("ALTER TABLE \"Timetable\" RENAME TO 'Timetable_Old';\n");
        database.execSQL("CREATE TABLE \"Timetable\" (\n" +
                "\t`id`\tINTEGER DEFAULT (null),\n" +
                "\t`IMS Eastney (Departures)`\tINTEGER DEFAULT (null),\n" +
                "\t`Langstone Campus (for Departures only)`\tINTEGER,\n" +
                "\t`Locksway Road (for Milton Park)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (adj Lidi)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (opp Fratton Station)`\tINTEGER,\n" +
                "\t`Winston Churchill Avenue (adj Ibis Hotel)`\tINTEGER,\n" +
                "\t`Cambridge Road (adj Student Union for Arrivals only)`\tINTEGER,\n" +
                "\t`Cambridge Road (adj Nuffield Building)`\tINTEGER,\n" +
                "\t`Winston Churchill Avenue (adj Law Courts)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (adj Fratton Station)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (opp Lidl)`\tINTEGER DEFAULT (null),\n" +
                "\t`Goldsmith Avenue (adj Milton Park)`\tINTEGER,\n" +
                "\t`IMS Eastney`\tINTEGER DEFAULT (null),\n" +
                "\t`Langstone Campus (for Arrivals only)`\tINTEGER,\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");\n");
        database.execSQL("INSERT INTO `Timetable` (id,'IMS Eastney (Departures)','Langstone Campus (for Departures only)'" +
                ",'Locksway Road (for Milton Park)','Goldsmith Avenue (adj Lidi)','Goldsmith Avenue (opp Fratton Station)'" +
                ",'Winston Churchill Avenue (adj Ibis Hotel)','Cambridge Road (adj Student Union for Arrivals only)'" +
                ",'Cambridge Road (adj Nuffield Building)','Winston Churchill Avenue (adj Law Courts)'" +
                ",'Goldsmith Avenue (adj Fratton Station)','Goldsmith Avenue (opp Lidl)','Goldsmith Avenue (adj Milton Park)'" +
                ",'IMS Eastney','Langstone Campus (for Arrivals only)')" +
                " VALUES (1,NULL,27600,27780,28020,28140,28380,28500,28800,29100,29280,29400,29640,NULL,29820),\n" +
                " (2,NULL,28800,28980,29220,29340,29580,29700,30000,30300,30480,30600,30840,NULL,31020),\n" +
                " (3,NULL,30000,30180,30420,30540,30780,30900,31200,31500,31680,31800,32040,NULL,32220),\n" +
                " (4,NULL,31200,31380,31620,31740,31980,32100,32400,32700,32880,33000,33240,NULL,33420),\n" +
                " (5,NULL,32400,32580,32820,32940,33180,33300,33600,33900,34080,34200,34440,NULL,34620),\n" +
                " (6,NULL,NULL,NULL,33300,33480,33600,33900,NULL,34080,34320,34500,34690,35400,NULL),\n" +
                " (7,NULL,33600,33780,34020,34140,34380,34500,34800,35100,35280,35400,35640,NULL,35820),\n" +
                " (8,NULL,34800,34980,35220,35340,35580,35700,36000,36300,36480,36600,36840,NULL,37020),\n" +
                " (9,NULL,36000,36180,36420,36540,36780,36900,37200,37500,37680,37800,38040,NULL,38220),\n" +
                " (10,36240,NULL,36720,36900,37080,37200,37500,NULL,37680,37920,38100,38280,39000,NULL),\n" +
                " (11,NULL,37200,37380,37620,37740,37980,38100,38400,38700,38880,39000,39240,NULL,39420),\n" +
                " (12,NULL,38400,38580,38820,38940,39180,39300,39600,39900,40080,40200,40440,NULL,40620),\n" +
                " (13,NULL,39600,39780,40020,40140,40380,40500,40800,41100,41280,41400,41640,NULL,41820),\n" +
                " (14,39840,NULL,40320,40500,40680,40800,41100,NULL,41280,41520,41700,41870,42600,NULL),\n" +
                " (15,NULL,40800,40980,41220,41340,41580,41700,42000,42300,42480,42600,42840,NULL,43020),\n" +
                " (16,NULL,42000,42180,42420,42540,42780,42900,43200,43500,43680,43800,44040,NULL,44220),\n" +
                " (17,NULL,43200,43380,43620,43740,43980,44100,44400,44700,44880,45000,45240,NULL,45420),\n" +
                " (18,43440,NULL,43920,44100,44280,44400,44700,NULL,44880,45120,45300,45460,46800,47520),\n" +
                " (19,NULL,44400,44580,44820,44940,45180,45300,45600,45900,46080,46200,46440,NULL,46620),\n" +
                " (20,NULL,45600,45780,46020,46140,46380,46500,46800,47100,47280,47400,47640,NULL,47820),\n" +
                " (21,NULL,46800,46980,47220,47340,47580,47700,48000,48300,48480,48600,48840,NULL,49020),\n" +
                " (22,NULL,48000,48180,48420,48540,48780,48900,49200,49500,49680,49800,50040,NULL,50220),\n" +
                " (23,NULL,49200,49380,49620,49740,49980,50100,50400,50700,50880,51000,51240,NULL,51420),\n" +
                " (24,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),\n" +
                " (25,NULL,50400,50580,50820,50940,51180,51300,51600,51900,52080,52200,52440,NULL,52620),\n" +
                " (26,NULL,51600,51780,52020,52140,52380,52500,52800,53100,53280,53400,53640,NULL,53820),\n" +
                " (27,NULL,52800,52980,53220,53340,53580,53700,54000,54300,54480,54600,54840,NULL,55020),\n" +
                " (28,NULL,54000,54180,54420,54540,54780,54900,55200,55500,55680,55800,56040,NULL,56220),\n" +
                " (29,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),\n" +
                " (30,NULL,55200,55380,55620,55740,55980,56100,56400,56700,56880,57000,57240,NULL,57420),\n" +
                " (31,NULL,56400,56580,56820,56940,57180,57300,57600,57900,58080,58200,58440,NULL,58620),\n" +
                " (32,NULL,57600,57780,58020,58140,58380,58500,58800,59100,59280,59400,59640,NULL,59820),\n" +
                " (33,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),\n" +
                " (34,NULL,58800,58980,59220,59340,59580,59700,60000,60300,60480,60600,60840,NULL,61020),\n" +
                " (35,NULL,60000,60180,60420,60540,60780,60900,61200,61500,61680,61800,62040,NULL,62220),\n" +
                " (36,NULL,61200,61380,61620,61740,61980,62100,62400,62700,62880,63000,63240,NULL,63420),\n" +
                " (37,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),\n" +
                " (38,NULL,62400,62580,62820,62940,63180,63300,63600,63900,64080,64200,64440,NULL,64620),\n" +
                " (39,NULL,63600,63780,64020,64140,64380,64500,64800,65100,65280,65400,65640,NULL,65820),\n" +
                " (40,NULL,64800,64980,65220,65340,65580,65700,66000,66300,66480,66600,66840,NULL,67020),\n" +
                " (41,NULL,66000,66180,66420,66540,66780,66900,67200,67500,67680,67800,68040,NULL,68220),\n" +
                " (42,NULL,67200,67380,67620,67740,67980,68100,68400,68700,68880,69000,69240,NULL,69420),\n" +
                " (43,NULL,68400,68580,68820,68940,69180,69300,69600,69900,70080,70200,70440,NULL,70620),\n" +
                " (44,NULL,74400,74580,74820,74940,75180,75300,75600,75900,76080,76200,76440,NULL,76620),\n" +
                " (45,NULL,76800,76980,77220,77340,77580,77700,78000,78300,78480,78600,78840,NULL,79020),\n" +
                " (46,NULL,79200,79380,79620,79740,79980,80100,80400,80700,80880,81000,81240,NULL,81420),\n" +
                " (47,NULL,81600,81780,82020,82140,82380,82500,82800,83100,83280,83400,83640,NULL,83820),\n" +
                " (48,NULL,NULL,NULL,NULL,NULL,NULL,NULL,600,780,900,1020,1200,NULL,1500);");
        database.execSQL("DROP TABLE \"Timetable_Old\"\n");
    }

    public static void migrateWeekendTimetable(SupportSQLiteDatabase database){
        database.execSQL("ALTER TABLE \"Timetable\" RENAME TO 'Timetable_Old';\n");
        database.execSQL("CREATE TABLE \"Timetable\" (\n" +
                "\t`id`\tINTEGER DEFAULT (null),\n" +
                "\t`IMS Eastney (Departures)`\tINTEGER DEFAULT (null),\n" +
                "\t`Langstone Campus (for Departures only)`\tINTEGER,\n" +
                "\t`Locksway Road (for Milton Park)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (adj Lidi)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (opp Fratton Station)`\tINTEGER,\n" +
                "\t`Winston Churchill Avenue (adj Ibis Hotel)`\tINTEGER,\n" +
                "\t`Cambridge Road (adj Student Union for Arrivals only)`\tINTEGER,\n" +
                "\t`Cambridge Road (adj Nuffield Building)`\tINTEGER,\n" +
                "\t`Winston Churchill Avenue (adj Law Courts)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (adj Fratton Station)`\tINTEGER,\n" +
                "\t`Goldsmith Avenue (opp Lidl)`\tINTEGER DEFAULT (null),\n" +
                "\t`Goldsmith Avenue (adj Milton Park)`\tINTEGER,\n" +
                "\t`IMS Eastney`\tINTEGER DEFAULT (null),\n" +
                "\t`Langstone Campus (for Arrivals only)`\tINTEGER,\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");\n");
        database.execSQL("INSERT INTO `Timetable` (id,'IMS Eastney (Departures)','Langstone Campus (for Departures only)'" +
                ",'Locksway Road (for Milton Park)','Goldsmith Avenue (adj Lidi)','Goldsmith Avenue (opp Fratton Station)'" +
                ",'Winston Churchill Avenue (adj Ibis Hotel)','Cambridge Road (adj Student Union for Arrivals only)'" +
                ",'Cambridge Road (adj Nuffield Building)','Winston Churchill Avenue (adj Law Courts)'" +
                ",'Goldsmith Avenue (adj Fratton Station)','Goldsmith Avenue (opp Lidl)','Goldsmith Avenue (adj Milton Park)'" +
                ",'IMS Eastney','Langstone Campus (for Arrivals only)')" +
                " VALUES (1,NULL,36000,36240,36480,36600,36780,36960,37200,37500,37680,37800,37980,NULL,NULL),\n" +
                " (2,NULL,38400,38640,38880,39000,39180,39360,39600,39900,40080,40200,40380,NULL,NULL),\n" +
                " (3,NULL,40800,41040,41280,41400,41580,41760,42000,42300,42480,42600,42780,NULL,NULL),\n" +
                " (4,NULL,43200,43440,43680,43800,43980,44160,44400,44700,44880,45000,45180,NULL,45360),\n" +
                " (5,NULL,48000,48240,48480,48600,48780,48960,49200,49500,49680,49800,49980,NULL,NULL),\n" +
                " (6,NULL,50400,50640,50880,51000,51180,51360,51600,51900,52080,52200,52380,NULL,NULL),\n" +
                " (7,NULL,52800,53040,53280,53400,53580,53760,54000,54300,54480,54600,54780,NULL,NULL),\n" +
                " (8,NULL,55200,55440,55680,55800,55980,56160,56400,56700,56880,57000,57180,NULL,57360),\n" +
                " (9,NULL,58800,59040,59280,59400,59580,59760,60000,60300,60480,60600,60780,NULL,NULL),\n" +
                " (10,NULL,61200,61440,61680,61800,61980,62160,62400,62700,62880,63000,63180,NULL,NULL),\n" +
                " (11,NULL,63600,63840,64080,64200,64380,64560,64800,65100,65280,65400,65580,NULL,65760);");
        database.execSQL("DROP TABLE \"Timetable_Old\"\n");
    }

}
