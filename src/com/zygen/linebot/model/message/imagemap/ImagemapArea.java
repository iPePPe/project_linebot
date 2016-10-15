/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.zygen.linebot.model.message.imagemap;

import lombok.Value;

@Value
public class ImagemapArea {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    public ImagemapArea(int x, int y, int width,int height){
    	this.x = x;
    	this.y = y;
    	this.width = width;
    	this.height = height;
    }
}
