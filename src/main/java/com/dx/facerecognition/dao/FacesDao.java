package com.dx.facerecognition.dao;/**
 * Created by Administrator on 2019/5/11.
 *
 * @author Administrator
 */

import com.dx.facerecognition.entity.Faces;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 *@ClassName FacesDao
 *@Description TODO
 *@Autor Administrator
 *@Date 2019/5/11 21:46
 **/
@Mapper
public interface FacesDao {
    public List<Faces> getFacesByUserId(int userid);

    public int insertNewFace(Faces faces);

    public int deleteFaceByUserId(int userid);

    public String getOldestFacePath(int userid);
}
