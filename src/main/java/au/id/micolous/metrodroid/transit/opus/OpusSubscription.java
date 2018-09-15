/*
 * OpusSubscription.java
 *
 * Copyright 2018 Google
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package au.id.micolous.metrodroid.transit.opus;

import android.os.Parcel;

import au.id.micolous.metrodroid.transit.en1545.En1545Bitmap;
import au.id.micolous.metrodroid.transit.en1545.En1545Container;
import au.id.micolous.metrodroid.transit.en1545.En1545Field;
import au.id.micolous.metrodroid.transit.en1545.En1545FixedHex;
import au.id.micolous.metrodroid.transit.en1545.En1545FixedInteger;
import au.id.micolous.metrodroid.transit.en1545.En1545Lookup;
import au.id.micolous.metrodroid.transit.en1545.En1545Subscription;
import au.id.micolous.metrodroid.util.Utils;

class OpusSubscription extends En1545Subscription {

    public static final Creator<OpusSubscription> CREATOR = new Creator<OpusSubscription>() {
        public OpusSubscription createFromParcel(Parcel parcel) {
            return new OpusSubscription(parcel);
        }

        public OpusSubscription[] newArray(int size) {
            return new OpusSubscription[size];
        }
    };
    private static final En1545Field FIELDS = new En1545Container(
            new En1545FixedInteger("UnknownA", 3),
            new En1545Bitmap(
                    new En1545FixedInteger("ContractProvider", 8),
                    new En1545FixedInteger("ContractTariff", 16),
                    new En1545Bitmap(
                            En1545FixedInteger.date("ContractStart"),
                            En1545FixedInteger.date("ContractEnd")
                    ),
                    new En1545Container(
                            new En1545FixedInteger("UnknownC", 17),
                            En1545FixedInteger.date("ContractSale"),
			    En1545FixedInteger.time("ContractSale"),
                            new En1545FixedHex("UnknownD", 80)
                    )
            )
    );

    private final int mTicketsRemaining;

    public OpusSubscription(byte[] dataSub, byte[] dataCtr, int num) {
        super(dataSub, FIELDS, num);
        mTicketsRemaining = dataCtr == null ? 0 : Utils.getBitsFromBuffer(dataCtr, 16, 8);
    }

    @Override
    protected En1545Lookup getLookup() {
        return OpusLookup.getInstance();
    }

    @Override
    protected Integer getCounter() {
        return mParsed.getIntOrZero("ContractEndDate") == 0 ? mTicketsRemaining : null;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(mTicketsRemaining);
    }

    private OpusSubscription(Parcel parcel) {
        super(parcel);
        mTicketsRemaining = parcel.readInt();
    }
}
